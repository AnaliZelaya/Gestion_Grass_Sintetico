package com.unas.grassbackend.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class AbrirTurnoRequest {
    private UUID idCaja;
    private BigDecimal montoInicial;
    private UUID idUsuario; // Identificador del cajero

    // Getters y Setters
    public UUID getIdCaja() { return idCaja; }
    public void setIdCaja(UUID idCaja) { this.idCaja = idCaja; }
    
    public BigDecimal getMontoInicial() { return montoInicial; }
    public void setMontoInicial(BigDecimal montoInicial) { this.montoInicial = montoInicial; }
    
    public UUID getIdUsuario() { return idUsuario; }
    public void setIdUsuario(UUID idUsuario) { this.idUsuario = idUsuario; }
}
