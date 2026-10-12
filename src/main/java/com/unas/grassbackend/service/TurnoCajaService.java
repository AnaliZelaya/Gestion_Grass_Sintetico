package com.unas.grassbackend.service;

import com.unas.grassbackend.dto.AbrirTurnoRequest;
import com.unas.grassbackend.dto.TurnoCajaResponseDTO; // 🟢 NUEVO
import com.unas.grassbackend.entity.TurnoCaja;

import java.util.List; // NUEVO
import java.util.Optional;
import java.util.UUID;

public interface TurnoCajaService {
    Optional<TurnoCaja> obtenerTurnoActivo(UUID idUsuario);
    TurnoCaja abrirTurno(AbrirTurnoRequest request);
    void cerrarTurno(UUID idTurno, double montoReal);
    List<TurnoCajaResponseDTO> listarHistorial(); //NUEVO
}