package com.unas.grassbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TurnoCajaResponseDTO {
    private UUID idTurno;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private BigDecimal montoInicial;
    private BigDecimal montoCalculado;
    private BigDecimal montoReal;
    private BigDecimal diferencia;
    private String estado;
    
    // Solo mandamos los nombres, no los objetos completos
    private String nombreCaja;
    private String nombreUsuario;

    public TurnoCajaResponseDTO() {}

    // Getters y Setters
    public UUID getIdTurno() { return idTurno; }
    public void setIdTurno(UUID idTurno) { this.idTurno = idTurno; }

    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }

    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }

    public BigDecimal getMontoInicial() { return montoInicial; }
    public void setMontoInicial(BigDecimal montoInicial) { this.montoInicial = montoInicial; }

    public BigDecimal getMontoCalculado() { return montoCalculado; }
    public void setMontoCalculado(BigDecimal montoCalculado) { this.montoCalculado = montoCalculado; }

    public BigDecimal getMontoReal() { return montoReal; }
    public void setMontoReal(BigDecimal montoReal) { this.montoReal = montoReal; }

    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNombreCaja() { return nombreCaja; }
    public void setNombreCaja(String nombreCaja) { this.nombreCaja = nombreCaja; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
}
