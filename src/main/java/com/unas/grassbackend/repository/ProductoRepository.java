package com.unas.grassbackend.repository;

import com.unas.grassbackend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoRepository extends JpaRepository<Producto, UUID> {
    Optional<Producto> findByCodigo(String codigo);
    
    // NUEVO: Método para buscar productos filtrados por Sede
    List<Producto> findBySede_IdSede(Integer idSede);
}