package com.unas.grassbackend.scheduler;

import com.unas.grassbackend.entity.Reserva;
import com.unas.grassbackend.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class ReservaScheduler {

    @Autowired
    private ReservaRepository reservaRepository;

    // 🟢 Se ejecuta automáticamente cada 60,000 milisegundos (1 minuto)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void actualizarEstadosReservas() {
        LocalDateTime ahora = LocalDateTime.now();

        // 1. Solo traemos las reservas que nos interesa vigilar
        List<Reserva> reservasActivas = reservaRepository.findByEstadoIn(
                Arrays.asList("Confirmada", "En Curso", "Pendiente")
        );

        for (Reserva reserva : reservasActivas) {
            LocalDateTime inicio = reserva.getHoraInicio();
            LocalDateTime fin = reserva.getHoraFin();

            // LÓGICA DEL DIAGRAMA DE ESTADOS:

            // CASO A: Ya pasó la hora de fin -> "Finalizada"
            if (ahora.isAfter(fin) || ahora.isEqual(fin)) {
                reserva.setEstado("Finalizada");
                reservaRepository.save(reserva);
                System.out.println("✅ Reserva " + reserva.getIdReserva() + " marcada como Finalizada.");
            }
            // CASO B: Ya llegó la hora de jugar y estaba confirmada -> "En Curso"
            else if ((ahora.isAfter(inicio) || ahora.isEqual(inicio)) && reserva.getEstado().equals("Confirmada")) {
                reserva.setEstado("En Curso");
                reservaRepository.save(reserva);
                System.out.println("▶️ Reserva " + reserva.getIdReserva() + " marcada como En Curso.");
            }
            // CASO C: Llegó la hora del partido pero NUNCA pagaron (Sigue Pendiente) -> "Cancelada"
            else if ((ahora.isAfter(inicio) || ahora.isEqual(inicio)) && reserva.getEstado().equals("Pendiente")) {
                reserva.setEstado("Cancelada");
                reservaRepository.save(reserva);
                System.out.println("❌ Reserva " + reserva.getIdReserva() + " Cancelada por falta de pago.");
            }
        }
    }
}