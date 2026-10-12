package com.unas.grassbackend.service;

import com.unas.grassbackend.entity.Cancha;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CanchaService {
    List<Cancha> listarTodas();
    List<Cancha> listarDisponibles();
    List<Cancha> buscarDisponibles(LocalDateTime inicio, LocalDateTime fin);
    Cancha guardar(Cancha cancha);
    Cancha actualizar(UUID id, Cancha cancha);
    void eliminar(UUID id);
}
