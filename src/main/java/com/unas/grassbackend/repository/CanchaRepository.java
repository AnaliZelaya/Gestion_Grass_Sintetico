package com.unas.grassbackend.repository;

import com.unas.grassbackend.entity.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, UUID> {
    
    List<Cancha> findByEstado(String estado);

    // BUSCADOR MÁGICO: Trae las canchas que NO tienen cruces en el horario pedido
    @Query(value = "SELECT * FROM canchas c WHERE c.estado = 'Disponible' AND c.id_cancha NOT IN (" +
                   "SELECT r.id_cancha FROM reservas r WHERE " +
                   "(:inicio < r.hora_fin AND :fin > r.hora_inicio))", 
           nativeQuery = true)
    List<Cancha> buscarCanchasLibres(@Param("inicio") LocalDateTime inicio, 
                                     @Param("fin") LocalDateTime fin);
}