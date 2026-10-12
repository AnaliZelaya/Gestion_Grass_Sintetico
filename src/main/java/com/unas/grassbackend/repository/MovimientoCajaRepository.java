package com.unas.grassbackend.repository;

import com.unas.grassbackend.entity.MovimientoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, UUID> {
    
	List<MovimientoCaja> findByTurno_IdTurno(UUID idTurno);
    // Aquí puedes agregar consultas personalizadas más adelante si las necesitas.
    // Por ahora, JpaRepository ya te regala el .save(), .findAll(), .findById(), etc.
    
}