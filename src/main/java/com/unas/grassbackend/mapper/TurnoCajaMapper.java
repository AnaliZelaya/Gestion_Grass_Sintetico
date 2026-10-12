package com.unas.grassbackend.mapper;

import com.unas.grassbackend.dto.TurnoCajaResponseDTO;
import com.unas.grassbackend.entity.TurnoCaja;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TurnoCajaMapper {

    public TurnoCajaResponseDTO toDTO(TurnoCaja turno) {
        if (turno == null) return null;

        TurnoCajaResponseDTO dto = new TurnoCajaResponseDTO();
        dto.setIdTurno(turno.getIdTurno());
        dto.setFechaApertura(turno.getFechaApertura());
        dto.setFechaCierre(turno.getFechaCierre());
        dto.setMontoInicial(turno.getMontoInicial());
        dto.setMontoCalculado(turno.getMontoCalculado());
        dto.setMontoReal(turno.getMontoReal());
        dto.setDiferencia(turno.getDiferencia());
        dto.setEstado(turno.getEstado());

        if (turno.getCaja() != null) {
            dto.setNombreCaja(turno.getCaja().getNombre());
        }
        if (turno.getUsuario() != null) {
            dto.setNombreUsuario(turno.getUsuario().getNombre());
        }

        return dto;
    }

    public List<TurnoCajaResponseDTO> toDTOList(List<TurnoCaja> turnos) {
        return turnos.stream().map(this::toDTO).collect(Collectors.toList());
    }
}