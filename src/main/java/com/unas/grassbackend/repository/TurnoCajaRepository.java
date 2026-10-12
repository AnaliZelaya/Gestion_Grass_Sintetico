package com.unas.grassbackend.repository;

import com.unas.grassbackend.entity.TurnoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; 
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TurnoCajaRepository extends JpaRepository<TurnoCaja, UUID> {
    
    // CRÍTICO: Saber si el cajero ya tiene un turno abierto para bloquear o permitir el paso
    Optional<TurnoCaja> findByUsuario_IdUsuarioAndEstado(UUID idUsuario, String estado);
    
    List<TurnoCaja> findByCajaSedeIdSedeOrderByFechaAperturaDesc(Long idSede);

    //NUEVO: Busca el primer turno de caja que esté "Abierta" en una sede específica
    Optional<TurnoCaja> findFirstByEstadoIgnoreCaseAndCajaSedeIdSede(String estado, Long idSede);
}