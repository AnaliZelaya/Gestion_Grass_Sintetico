package com.unas.grassbackend.service;

import com.unas.grassbackend.dto.ReservaResponseDTO;
import com.unas.grassbackend.dto.ClienteFidelizacionDTO;
import com.unas.grassbackend.entity.Reserva;
import com.unas.grassbackend.entity.Cancha;
import com.unas.grassbackend.entity.TurnoCaja;
import com.unas.grassbackend.entity.MovimientoCaja;
import com.unas.grassbackend.entity.PagoReserva;
import com.unas.grassbackend.mapper.ReservaMapper; 
import com.unas.grassbackend.repository.ReservaRepository;
import com.unas.grassbackend.repository.CanchaRepository;
import com.unas.grassbackend.repository.TurnoCajaRepository;
import com.unas.grassbackend.repository.MovimientoCajaRepository;
import com.unas.grassbackend.repository.PagoReservaRepository;
import com.unas.grassbackend.security.SedeContextHolder;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private ReservaMapper reservaMapper; 
    
    @Autowired
    private TurnoCajaRepository turnoCajaRepository;

    @Autowired
    private MovimientoCajaRepository movimientoCajaRepository;

    @Autowired
    private PagoReservaRepository pagoReservaRepository;

    @Override
    public List<ReservaResponseDTO> listarTodas() {
        String sedeActiva = SedeContextHolder.getSede();
        List<Reserva> reservas;

        if (sedeActiva == null || "GLOBAL".equals(sedeActiva)) {
            reservas = reservaRepository.findAll();
        } else {
            try {
                Integer idSede = Integer.parseInt(sedeActiva); 
                reservas = reservaRepository.findBySedeId(idSede); 
            } catch (NumberFormatException e) {
                reservas = reservaRepository.findAll();
            }
        }
        
        return reservaMapper.toDTOList(reservas);
    }

    @Override
    public List<ReservaResponseDTO> listarPorUsuario(UUID idUsuario) {
        List<Reserva> reservas = reservaRepository.findByUsuarioIdUsuarioOrderByHoraInicioDesc(idUsuario);
        return reservaMapper.toDTOList(reservas);
    }

    @Override
    public Reserva realizarReserva(Reserva reserva) {
        if (reserva.getHoraInicio().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se pueden realizar reservas en fechas u horas pasadas.");
        }

        int horaApertura = 8;
        int horaCierre = 23;

        int inicioH = reserva.getHoraInicio().getHour();
        int finH = reserva.getHoraFin().getHour();
        int finM = reserva.getHoraFin().getMinute();

        if (inicioH < horaApertura || inicioH >= horaCierre || finH > horaCierre || (finH == horaCierre && finM > 0)) {
            throw new RuntimeException("El local solo atiende de 08:00 AM a 11:00 PM.");
        }

        boolean ocupado = reservaRepository.existeCruce(
                reserva.getCancha().getIdCancha(), 
                reserva.getHoraInicio(), 
                reserva.getHoraFin()
        );

        if (ocupado) {
            throw new RuntimeException("Lo sentimos, la cancha ya está reservada en ese horario.");
        }

        Cancha cancha = canchaRepository.findById(reserva.getCancha().getIdCancha())
                .orElseThrow(() -> new RuntimeException("La cancha no existe"));

        if (!cancha.getEstado().equalsIgnoreCase("Disponible")) {
            throw new RuntimeException("Lo sentimos, esta cancha no está disponible para reservas en este momento.");
        }

        long minutos = Duration.between(reserva.getHoraInicio(), reserva.getHoraFin()).toMinutes();
        
        if (minutos < 60) {
            throw new RuntimeException("El tiempo mínimo de reserva es de 60 minutos.");
        }
        
        double horas = minutos / 60.0;
        
        double precioAplicado;
        if (reserva.getHoraInicio().getHour() >= 18) {
            precioAplicado = cancha.getPrecioNoche();
        } else {
            precioAplicado = cancha.getPrecioDia();
        }
        
        double totalCalculado = precioAplicado * horas;
        reserva.setTotal(BigDecimal.valueOf(totalCalculado));
        reserva.setFechaReserva(LocalDateTime.now());
        
        reserva.setEstado("Pendiente");

        return reservaRepository.save(reserva);
    }
    
    @Override
    @Transactional
    public Reserva aprobarReserva(UUID idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        if (!"Pendiente".equals(reserva.getEstado())) {
            throw new RuntimeException("La reserva no está en estado Pendiente");
        }
        
        reserva.setEstado("Confirmada");
        return reservaRepository.save(reserva);
    }
    
    // 🟢 MÉTODO REFACTORIZADO: Ahora recibe y guarda el código de operación (seguridad)
    @Override
    @Transactional
    public Reserva completarPago(UUID id, String metodoPago, BigDecimal monto, String rolUsuario, String codigoOperacion) {
        // 1. Buscar la reserva
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // 2. Determinar a qué sede pertenece este dinero
        String sedeActiva = SedeContextHolder.getSede();
        Long idSede = null;
        
        if (sedeActiva == null || "GLOBAL".equals(sedeActiva)) {
            if (reserva.getCancha() != null && reserva.getCancha().getSede() != null) {
            	idSede = reserva.getCancha().getSede().getIdSede().longValue();
            } else {
                throw new RuntimeException("Error: La cancha no tiene una sede válida asignada.");
            }
        } else {
            idSede = Long.parseLong(sedeActiva);
        }

        // 3. Buscar caja abierta OBLIGATORIA
        TurnoCaja turnoActivo = turnoCajaRepository.findFirstByEstadoIgnoreCaseAndCajaSedeIdSede("ABIERTO", idSede)
                .orElseThrow(() -> new RuntimeException("¡Atención! No hay ninguna caja abierta en la sede de esta cancha. El administrador local debe abrir caja primero para poder registrar contablemente tu cobro."));

        // 4. Registrar el ingreso en la caja
        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setTurno(turnoActivo);
        movimiento.setTipoMovimiento("INGRESO");
        
        String notaCobro = "SUPER_ADMIN".equals(rolUsuario) ? " (Cobrado por SuperAdmin)" : "";
        movimiento.setConcepto("Pago Reserva - Cliente: " + (reserva.getNombreCliente() != null ? reserva.getNombreCliente() : "Desconocido") + notaCobro);
        
        movimiento.setMetodoPago(metodoPago);
        movimiento.setMonto(monto);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        
        // 🟢 ASIGNACIÓN DEL CÓDIGO DE OPERACIÓN (YAPE/PLIN)
        movimiento.setCodigoSeguridad(codigoOperacion);

        movimiento = movimientoCajaRepository.save(movimiento);

        // 5. Generar el historial del comprobante de reserva
        PagoReserva pago = new PagoReserva();
        pago.setReserva(reserva);
        pago.setMovimiento(movimiento);
        pago.setMonto(monto);
        pago.setTipoPago(metodoPago);
        pago.setFechaPago(LocalDateTime.now());
        pagoReservaRepository.save(pago);

        // 6. Actualizar los montos de la reserva
        BigDecimal adelantoActual = reserva.getAdelanto() != null ? reserva.getAdelanto() : BigDecimal.ZERO;
        BigDecimal nuevoAdelanto = adelantoActual.add(monto);
        reserva.setAdelanto(nuevoAdelanto);

        if (nuevoAdelanto.compareTo(reserva.getTotal()) >= 0) {
            reserva.setEstado("Confirmada"); 
        }

        return reservaRepository.save(reserva);
    }
    
    @Override
    @Transactional
    public void cancelarReserva(UUID id) {
        // 1. Buscamos la reserva que se quiere anular
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // 2. Verificamos si la reserva tenía algún dinero adelantado
        BigDecimal adelanto = reserva.getAdelanto() != null ? reserva.getAdelanto() : BigDecimal.ZERO;

        if (adelanto.compareTo(BigDecimal.ZERO) > 0) {
            // Buscamos a qué sede pertenece la cancha de esta reserva
            String sedeActiva = SedeContextHolder.getSede();
            Long idSede = null;
            
            if (sedeActiva == null || "GLOBAL".equals(sedeActiva)) {
                if (reserva.getCancha() != null && reserva.getCancha().getSede() != null) {
                    idSede = reserva.getCancha().getSede().getIdSede().longValue();
                } else {
                    throw new RuntimeException("Error: La cancha no tiene una sede válida asignada.");
                }
            } else {
                idSede = Long.parseLong(sedeActiva);
            }

            // Verificamos que la caja esté abierta para poder retirar el dinero
            TurnoCaja turnoActivo = turnoCajaRepository.findFirstByEstadoIgnoreCaseAndCajaSedeIdSede("ABIERTO", idSede)
                    .orElseThrow(() -> new RuntimeException("¡Atención! No hay ninguna caja abierta para registrar la devolución del dinero. Abra caja primero."));

            // 3. Creamos el movimiento de salida (EGRESO)
            MovimientoCaja egreso = new MovimientoCaja();
            egreso.setTurno(turnoActivo);
            egreso.setTipoMovimiento("EGRESO");
            egreso.setConcepto("Devolución por Anulación de Reserva - Cliente: " + 
                    (reserva.getNombreCliente() != null ? reserva.getNombreCliente() : "Desconocido"));
            egreso.setMetodoPago("Efectivo"); // Se asume devolución física por defecto
            egreso.setMonto(adelanto);
            egreso.setFechaMovimiento(LocalDateTime.now());
            
            movimientoCajaRepository.save(egreso);
        }

        // 4. Borrado Lógico: En lugar de destruir la información con deleteById(), 
        // cambiamos el estado a "Cancelada" para mantener el rastro en Power BI.
        reserva.setEstado("Cancelada");
        reservaRepository.save(reserva);
    }
    @Override
    public BigDecimal calcularIngresos(int mes, int anio) {
        String sedeActiva = SedeContextHolder.getSede();
        
        if (sedeActiva == null || "GLOBAL".equals(sedeActiva)) {
            return BigDecimal.ZERO; 
        } else {
            try {
                Integer idSede = Integer.parseInt(sedeActiva);
                return reservaRepository.calcularIngresosPorMesYSede(mes, anio, idSede);
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
    }
    
    @Override
    public List<ClienteFidelizacionDTO> obtenerFidelizacionClientes() {
        String sedeActiva = SedeContextHolder.getSede();
        List<Reserva> reservas;

        if (sedeActiva == null || "GLOBAL".equals(sedeActiva)) {
            reservas = reservaRepository.findAll();
        } else {
            try {
                Integer idSede = Integer.parseInt(sedeActiva);
                reservas = reservaRepository.findBySedeId(idSede);
            } catch (NumberFormatException e) {
                reservas = reservaRepository.findAll();
            }
        }

        Map<String, List<Reserva>> reservasPorCliente = reservas.stream()
                .filter(r -> r.getNombreCliente() != null && !r.getNombreCliente().trim().isEmpty() &&
                             ("Confirmada".equalsIgnoreCase(r.getEstado()) || "Finalizada".equalsIgnoreCase(r.getEstado())))
                .collect(Collectors.groupingBy(
                        r -> r.getNombreCliente().trim().toLowerCase() + "-" + 
                             (r.getTelefonoCliente() != null ? r.getTelefonoCliente().trim() : "sin-telefono")
                ));

        List<ClienteFidelizacionDTO> listaFidelidad = new ArrayList<>();

        for (Map.Entry<String, List<Reserva>> entry : reservasPorCliente.entrySet()) {
            List<Reserva> resCliente = entry.getValue();
            Reserva dataBase = resCliente.get(0); 

            ClienteFidelizacionDTO dto = new ClienteFidelizacionDTO();
            dto.setIdUsuario(UUID.randomUUID());
            dto.setNombre(dataBase.getNombreCliente()); 
            dto.setEmail(dataBase.getUsuario() != null ? dataBase.getUsuario().getEmail() : "Reserva en Local / Teléfono");
            dto.setTelefono(dataBase.getTelefonoCliente() != null ? dataBase.getTelefonoCliente() : "Sin celular");

            long partidos = resCliente.size();
            dto.setPartidosJugados(partidos);

            BigDecimal totalInvertido = resCliente.stream()
                    .map(r -> r.getTotal() != null ? r.getTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setDineroGenerado(totalInvertido);

            LocalDateTime ultimaFecha = resCliente.stream()
                    .map(Reserva::getHoraInicio)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            dto.setUltimaVisita(ultimaFecha);

            if (partidos >= 15) dto.setNivelFidelidad("DIAMANTE");
            else if (partidos >= 10) dto.setNivelFidelidad("ORO");
            else if (partidos >= 5) dto.setNivelFidelidad("PLATA");
            else dto.setNivelFidelidad("BRONCE");

            listaFidelidad.add(dto);
        }

        listaFidelidad.sort((a, b) -> Long.compare(b.getPartidosJugados(), a.getPartidosJugados()));
        return listaFidelidad;
    }
}