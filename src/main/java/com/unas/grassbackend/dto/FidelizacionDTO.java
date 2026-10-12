package com.unas.grassbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FidelizacionDTO {
    
    private String cliente;
    private String telefono;
    private Long partidosJugados;
    private BigDecimal dineroGenerado;
    private LocalDateTime ultimaVisita;

    // El constructor EXACTO que usa JPQL en el Repository
    public FidelizacionDTO(String cliente, String telefono, Long partidosJugados, BigDecimal dineroGenerado, LocalDateTime ultimaVisita) {
        this.cliente = cliente;
        this.telefono = telefono;
        this.partidosJugados = partidosJugados;
        this.dineroGenerado = dineroGenerado;
        this.ultimaVisita = ultimaVisita;
    }

    // Getters y Setters (Necesarios para que Spring los serialice a JSON para el Frontend)
    
    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Long getPartidosJugados() {
        return partidosJugados;
    }

    public void setPartidosJugados(Long partidosJugados) {
        this.partidosJugados = partidosJugados;
    }

    public BigDecimal getDineroGenerado() {
        return dineroGenerado;
    }

    public void setDineroGenerado(BigDecimal dineroGenerado) {
        this.dineroGenerado = dineroGenerado;
    }

    public LocalDateTime getUltimaVisita() {
        return ultimaVisita;
    }

    public void setUltimaVisita(LocalDateTime ultimaVisita) {
        this.ultimaVisita = ultimaVisita;
    }
}