package com.unas.grassbackend.repository;

import com.unas.grassbackend.entity.Caja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CajaRepository extends JpaRepository<Caja, UUID> {
    
    // Lista las cajas de una sede específica que estén ACTIVAS
    List<Caja> findBySede_IdSedeAndEstado(Long idSede, String estado);
}