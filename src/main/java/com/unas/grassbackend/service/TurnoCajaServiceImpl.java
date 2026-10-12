package com.unas.grassbackend.service;

import com.unas.grassbackend.dto.AbrirTurnoRequest;
import com.unas.grassbackend.dto.TurnoCajaResponseDTO;
import com.unas.grassbackend.entity.Caja;
import com.unas.grassbackend.entity.MovimientoCaja;
import com.unas.grassbackend.entity.TurnoCaja;
import com.unas.grassbackend.entity.Usuario;
import com.unas.grassbackend.mapper.TurnoCajaMapper;
import com.unas.grassbackend.repository.CajaRepository;
import com.unas.grassbackend.repository.MovimientoCajaRepository;
import com.unas.grassbackend.repository.TurnoCajaRepository;
import com.unas.grassbackend.repository.UsuarioRepository;
import com.unas.grassbackend.security.SedeContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TurnoCajaServiceImpl implements TurnoCajaService {

    @Autowired private TurnoCajaRepository turnoCajaRepository;
    @Autowired private CajaRepository cajaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    
    // 🟢 NUEVO: Inyectamos el repositorio de movimientos para hacer el cuadre matemático
    @Autowired private MovimientoCajaRepository movimientoCajaRepository;
    
    @Autowired private TurnoCajaMapper turnoCajaMapper;

    @Override
    public Optional<TurnoCaja> obtenerTurnoActivo(UUID idUsuario) {
        return turnoCajaRepository.findByUsuario_IdUsuarioAndEstado(idUsuario, "ABIERTO");
    }

    @Override
    public TurnoCaja abrirTurno(AbrirTurnoRequest request) {
        if (obtenerTurnoActivo(request.getIdUsuario()).isPresent()) {
            throw new RuntimeException("El usuario ya tiene un turno abierto.");
        }
        Caja caja = cajaRepository.findById(request.getIdCaja()).orElseThrow(() -> new RuntimeException("Caja no existe."));
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario()).orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        TurnoCaja nuevoTurno = new TurnoCaja();
        nuevoTurno.setCaja(caja);
        nuevoTurno.setUsuario(usuario);
        nuevoTurno.setMontoInicial(request.getMontoInicial());
        nuevoTurno.setMontoCalculado(request.getMontoInicial());
        nuevoTurno.setEstado("ABIERTO");
        return turnoCajaRepository.save(nuevoTurno);
    }

    // 🟢 MÉTODO REFACTORIZADO: Lógica contable de Cuadre de Caja
    @Override
    @Transactional
    public void cerrarTurno(UUID idTurno, double montoReal) {
        TurnoCaja turno = turnoCajaRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if ("CERRADO".equalsIgnoreCase(turno.getEstado())) {
            throw new RuntimeException("Este turno de caja ya se encuentra cerrado.");
        }

        // 1. Obtenemos todos los movimientos que ocurrieron en este turno específico
        List<MovimientoCaja> movimientos = movimientoCajaRepository.findByTurno_IdTurno(idTurno);

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalEgresos = BigDecimal.ZERO;

        // 2. Sumamos y restamos según el tipo de movimiento
        for (MovimientoCaja m : movimientos) {
            if ("INGRESO".equalsIgnoreCase(m.getTipoMovimiento())) {
                totalIngresos = totalIngresos.add(m.getMonto());
            } else if ("EGRESO".equalsIgnoreCase(m.getTipoMovimiento())) {
                totalEgresos = totalEgresos.add(m.getMonto());
            }
        }

        // 3. Calculamos el monto esperado (Fórmula: Inicial + Ingresos - Egresos)
        BigDecimal montoCalculadoEsperado = turno.getMontoInicial().add(totalIngresos).subtract(totalEgresos);
        BigDecimal montoFisicoDeclarado = BigDecimal.valueOf(montoReal);

        // 4. Sellamos la caja con los valores finales
        turno.setMontoCalculado(montoCalculadoEsperado);
        turno.setMontoReal(montoFisicoDeclarado);
        turno.setDiferencia(montoFisicoDeclarado.subtract(montoCalculadoEsperado)); // Si es negativo, falta dinero. Si es positivo, sobra.
        turno.setFechaCierre(LocalDateTime.now());
        turno.setEstado("CERRADO");

        turnoCajaRepository.save(turno);
    }

    @Override
    public List<TurnoCajaResponseDTO> listarHistorial() {
        String sedeActiva = SedeContextHolder.getSede();
        List<TurnoCaja> turnos = (sedeActiva == null || "GLOBAL".equals(sedeActiva)) 
            ? turnoCajaRepository.findAll() 
            : turnoCajaRepository.findByCajaSedeIdSedeOrderByFechaAperturaDesc(Long.parseLong(sedeActiva));
        return turnoCajaMapper.toDTOList(turnos);
    }
}