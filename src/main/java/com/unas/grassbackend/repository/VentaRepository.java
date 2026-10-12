package com.unas.grassbackend.repository;

import com.unas.grassbackend.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VentaRepository extends JpaRepository<Venta, UUID> {
    
    // FILTRO "OJO DE DIOS": Trae solo las ventas de una sede específica
    @Query("SELECT v FROM Venta v WHERE v.sede.idSede = :idSede")
    List<Venta> findBySedeId(@Param("idSede") Long idSede);
}