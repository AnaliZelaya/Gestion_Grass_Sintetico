package com.unas.grassbackend.controller;

import com.unas.grassbackend.dto.AbrirTurnoRequest;
import com.unas.grassbackend.dto.TurnoCajaResponseDTO;
import com.unas.grassbackend.entity.TurnoCaja;
import com.unas.grassbackend.service.TurnoCajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/turnos")
public class TurnoCajaController {

    @Autowired private TurnoCajaService turnoCajaService;

    @GetMapping
    public ResponseEntity<List<TurnoCajaResponseDTO>> listarHistorial() {
        return ResponseEntity.ok(turnoCajaService.listarHistorial());
    }

    @GetMapping("/estado")
    public ResponseEntity<?> verificarEstadoTurno(@RequestParam UUID idUsuario) {
        Optional<TurnoCaja> turno = turnoCajaService.obtenerTurnoActivo(idUsuario);
        return turno.isPresent() ? ResponseEntity.ok(Map.of("idTurno", turno.get().getIdTurno(), "estado", "ABIERTO")) : ResponseEntity.notFound().build();
    }

    @PostMapping("/abrir")
    public ResponseEntity<?> abrirTurno(@RequestBody AbrirTurnoRequest request) {
        TurnoCaja turno = turnoCajaService.abrirTurno(request);
        return ResponseEntity.ok(Map.of("message", "Caja abierta", "idTurno", turno.getIdTurno()));
    }

    @PutMapping("/cerrar/{id}")
    public ResponseEntity<?> cerrarTurno(@PathVariable UUID id, @RequestBody Map<String, Double> payload) {
        turnoCajaService.cerrarTurno(id, payload.get("montoReal"));
        return ResponseEntity.ok(Map.of("message", "Caja cerrada correctamente"));
    }
}