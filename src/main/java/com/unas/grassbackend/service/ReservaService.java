package com.unas.grassbackend.service;

import com.unas.grassbackend.dto.ReservaResponseDTO;
import com.unas.grassbackend.entity.Reserva;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ReservaService {
    List<ReservaResponseDTO> listarTodas();
    List<ReservaResponseDTO> listarPorUsuario(UUID idUsuario);
    List<com.unas.grassbackend.dto.ClienteFidelizacionDTO> obtenerFidelizacionClientes();
    
    Reserva realizarReserva(Reserva reserva);
    Reserva completarPago(UUID id, String metodoPago, BigDecimal monto, String rolUsuario, String codigoOperacion);
    Reserva aprobarReserva(UUID idReserva);
    void cancelarReserva(UUID id);
    
    java.math.BigDecimal calcularIngresos(int mes, int anio);
}