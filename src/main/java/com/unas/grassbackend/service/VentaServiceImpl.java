package com.unas.grassbackend.service;

import com.unas.grassbackend.dto.VentaResponseDTO;
import com.unas.grassbackend.entity.DetalleVenta;
import com.unas.grassbackend.entity.MovimientoCaja;
import com.unas.grassbackend.entity.Producto;
import com.unas.grassbackend.entity.Sede;
import com.unas.grassbackend.entity.TurnoCaja;
import com.unas.grassbackend.entity.Venta;
import com.unas.grassbackend.mapper.VentaMapper;
import com.unas.grassbackend.repository.MovimientoCajaRepository;
import com.unas.grassbackend.repository.ProductoRepository;
import com.unas.grassbackend.repository.SedeRepository;
import com.unas.grassbackend.repository.TurnoCajaRepository;
import com.unas.grassbackend.repository.VentaRepository;
import com.unas.grassbackend.security.SedeContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private SedeRepository sedeRepository; 

    // 🟢 NUEVOS REPOSITORIOS INYECTADOS PARA EL FLUJO DE CAJA
    @Autowired
    private TurnoCajaRepository turnoCajaRepository;

    @Autowired
    private MovimientoCajaRepository movimientoCajaRepository;

    @Autowired
    private VentaMapper ventaMapper;

    @Override
    public List<VentaResponseDTO> listarTodas() {
        String sedeActiva = SedeContextHolder.getSede();
        List<Venta> ventas;

        if (sedeActiva == null || "GLOBAL".equals(sedeActiva)) {
            ventas = ventaRepository.findAll();
        } else {
            try {
                Long idSede = Long.parseLong(sedeActiva);
                ventas = ventaRepository.findBySedeId(idSede);
            } catch (NumberFormatException e) {
                ventas = ventaRepository.findAll();
            }
        }
        
        return ventaMapper.toDTOList(ventas);
    }

    @Override
    @Transactional
    public Venta realizarVenta(Venta venta) {
        String sedeActiva = SedeContextHolder.getSede();

        if (sedeActiva == null || "GLOBAL".equals(sedeActiva)) {
            throw new RuntimeException("No se puede realizar una venta sin una sede física seleccionada.");
        }

        Long idSedeLong = Long.parseLong(sedeActiva);
        Integer idSedeInt = Integer.parseInt(sedeActiva);

        // 1. Validar que exista caja abierta en esta sede ("ABIERTO")
        TurnoCaja turnoActivo = turnoCajaRepository.findFirstByEstadoIgnoreCaseAndCajaSedeIdSede("ABIERTO", idSedeLong)
                .orElseThrow(() -> new RuntimeException("¡Atención! No hay ninguna caja abierta en esta sede. El administrador local debe abrir caja primero para registrar la venta."));

        Sede sedeBD = sedeRepository.findById(idSedeInt)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada en la base de datos"));
        venta.setSede(sedeBD);

        venta.setFechaVenta(LocalDateTime.now());

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un producto.");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.setVenta(venta); 

            Producto productoDB = productoRepository.findById(detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (productoDB.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + productoDB.getNombre());
            }

            productoDB.setStock(productoDB.getStock() - detalle.getCantidad());
            productoRepository.save(productoDB);
        }

        // 2. Guardamos primero la venta
        Venta ventaGuardada = ventaRepository.save(venta);

        // 3. Registrar automáticamente el ingreso en la tabla `movimientos_caja`
        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setTurno(turnoActivo);
        movimiento.setTipoMovimiento("INGRESO");
        movimiento.setConcepto("Venta de Snacks / Productos - Cliente: " + (venta.getNombreCliente() != null ? venta.getNombreCliente() : "Público General"));
        movimiento.setMetodoPago(venta.getMetodoPago() != null ? venta.getMetodoPago() : "Efectivo");
        movimiento.setMonto(BigDecimal.valueOf(venta.getTotal() != null ? venta.getTotal() : 0.0));
        movimiento.setFechaMovimiento(LocalDateTime.now());
        
        // CORRECCIÓN: Si tu entidad MovimientoCaja tiene la relación directa con Venta
        movimiento.setVenta(ventaGuardada); 

        movimientoCajaRepository.save(movimiento);

        return ventaGuardada;
    }
}