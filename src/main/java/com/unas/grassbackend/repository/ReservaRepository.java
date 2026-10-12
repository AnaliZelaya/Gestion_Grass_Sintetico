package com.unas.grassbackend.repository;

import com.unas.grassbackend.entity.Reserva;
// Asegúrate de importar el DTO (ajusta el paquete si es necesario)
import com.unas.grassbackend.dto.FidelizacionDTO; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    
    // JPQL Estricto para blindar la fuga de datos por Sede
    @Query("SELECT r FROM Reserva r WHERE r.cancha.sede.idSede = :idSede")
    List<Reserva> findBySedeId(@Param("idSede") Integer idSede);
    
    List<Reserva> findByUsuarioIdUsuarioOrderByHoraInicioDesc(UUID idUsuario);
    
    List<Reserva> findByUsuario_IdUsuarioOrderByFechaReservaDesc(UUID idUsuario);
    
    // CORRECCIÓN CRÍTICA: Se añadió el filtro de sede para que no sume los ingresos globales
    @Query("SELECT COALESCE(SUM(r.total), 0) FROM Reserva r WHERE r.cancha.sede.idSede = :idSede AND EXTRACT(MONTH FROM r.horaInicio) = :mes AND EXTRACT(YEAR FROM r.horaInicio) = :anio")
    java.math.BigDecimal calcularIngresosPorMesYSede(@Param("mes") int mes, @Param("anio") int anio, @Param("idSede") Integer idSede);
    
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.cancha.idCancha = :idCancha " +
           "AND (:inicio < r.horaFin AND :fin > r.horaInicio)")
    boolean existeCruce(@Param("idCancha") UUID idCancha, 
                        @Param("inicio") LocalDateTime inicio, 
                        @Param("fin") LocalDateTime fin);

    List<Reserva> findByEstadoIn(List<String> estados);

    // =========================================================================
    // NUEVA CONSULTA: Agrupación real de clientes para Fidelización
    // =========================================================================
    @Query("SELECT new com.unas.grassbackend.dto.FidelizacionDTO(" +
            "r.nombreCliente, r.telefonoCliente, COUNT(r), SUM(r.total), MAX(r.horaInicio)) " +
            "FROM Reserva r " +
            "WHERE r.cancha.sede.idSede = :idSede AND r.estado = 'Finalizada' " +
            "GROUP BY r.nombreCliente, r.telefonoCliente " +
            "ORDER BY COUNT(r) DESC")
     List<FidelizacionDTO> obtenerFidelizacionPorSede(@Param("idSede") Integer idSede);
}