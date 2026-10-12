package com.unas.grassbackend.repository;

import com.unas.grassbackend.entity.PagoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PagoReservaRepository extends JpaRepository<PagoReserva, UUID> {
    
    // Para ver todos los pagos que se le han hecho a una reserva (ej. Adelanto + Saldo)
    List<PagoReserva> findByReserva_IdReservaOrderByFechaPagoAsc(UUID idReserva);
}
