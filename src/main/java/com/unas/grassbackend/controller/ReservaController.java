package com.unas.grassbackend.controller;

import com.unas.grassbackend.dto.ReservaResponseDTO; // 🟢 Importación del DTO
import com.unas.grassbackend.entity.Reserva;
import com.unas.grassbackend.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorUsuario(@PathVariable UUID idUsuario) {
        return ResponseEntity.ok(reservaService.listarPorUsuario(idUsuario));
    }

    @PostMapping
    public ResponseEntity<Reserva> crearReserva(@RequestBody Reserva reserva) {
        return ResponseEntity.ok(reservaService.realizarReserva(reserva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable UUID id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.ok(Map.of("message", "Reserva cancelada correctamente"));
    }
    
    @PutMapping("/{id}/pagar")
    public ResponseEntity<Reserva> completarPago(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> pagoData) {
        
        String metodoPago = (String) pagoData.getOrDefault("metodoPago", "Efectivo");
        BigDecimal monto = new BigDecimal(pagoData.get("monto").toString());
        // Capturamos el rol enviado por React
        String rolUsuario = (String) pagoData.getOrDefault("rol", "ADMIN");
        
        // Capturamos el código de operación para Yape o Plin
        String codigoOperacion = (String) pagoData.get("codigoOperacion");
        
        // 🟢 CORRECCIÓN: Pasamos los 5 argumentos requeridos por el servicio
        return ResponseEntity.ok(reservaService.completarPago(id, metodoPago, monto, rolUsuario, codigoOperacion));
    }
    
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<Reserva> aprobarReserva(@PathVariable UUID id) {
        return ResponseEntity.ok(reservaService.aprobarReserva(id));
    }
    
    @GetMapping("/fidelizacion")
    public ResponseEntity<List<com.unas.grassbackend.dto.ClienteFidelizacionDTO>> obtenerFidelizacion() {
        return ResponseEntity.ok(reservaService.obtenerFidelizacionClientes());
    }
    
}