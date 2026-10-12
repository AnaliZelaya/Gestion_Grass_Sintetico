package com.unas.grassbackend.repository;

import com.unas.grassbackend.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {
    
    // NUEVO: Método para buscar solo sedes activas
    List<Sede> findByEstado(String estado);
}