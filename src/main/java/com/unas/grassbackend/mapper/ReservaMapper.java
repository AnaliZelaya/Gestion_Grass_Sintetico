package com.unas.grassbackend.mapper;

import com.unas.grassbackend.dto.ReservaResponseDTO;
import com.unas.grassbackend.entity.Reserva;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservaMapper {

    // Convierte una sola Entidad a DTO
    public ReservaResponseDTO toDTO(Reserva reserva) {
        if (reserva == null) return null;

        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setIdReserva(reserva.getIdReserva());
        dto.setHoraInicio(reserva.getHoraInicio());
        dto.setHoraFin(reserva.getHoraFin());
        dto.setEstado(reserva.getEstado());
        dto.setTotal(reserva.getTotal());
        dto.setAdelanto(reserva.getAdelanto());
        dto.setNombreCliente(reserva.getNombreCliente());
        dto.setTelefonoCliente(reserva.getTelefonoCliente());
        
        dto.setCodigoOperacion(reserva.getCodigoOperacion());
        dto.setFechaReserva(reserva.getFechaReserva());

        // Mapeo seguro de relaciones: Cancha
        if (reserva.getCancha() != null) {
            dto.setIdCancha(reserva.getCancha().getIdCancha());
            dto.setNombreCancha(reserva.getCancha().getNombre());
            
            // SOLUCIÓN: Capturamos el nombre de la sede de forma segura
            if (reserva.getCancha().getSede() != null) {
                dto.setNombreSede(reserva.getCancha().getSede().getNombre());
            }
            
            // NUEVO: Llenamos el objeto anidado para que React pueda leer la imagen y el nombre
            dto.setCancha(new ReservaResponseDTO.CanchaResumenDTO(
                reserva.getCancha().getNombre(),
                reserva.getCancha().getUrlImagen()
            ));
        }

        // Mapeo seguro de relaciones: Usuario
        if (reserva.getUsuario() != null) {
            dto.setNombreUsuario(reserva.getUsuario().getNombre());
            
            // NUEVO: Pasamos el ID del usuario para los cruces y validaciones del frontend
            dto.setIdUsuario(reserva.getUsuario().getIdUsuario());
        }

        return dto;
    }

    // Convierte una Lista de Entidades a una Lista de DTOs
    public List<ReservaResponseDTO> toDTOList(List<Reserva> reservas) {
        return reservas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}