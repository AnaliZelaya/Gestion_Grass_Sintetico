package com.unas.grassbackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "turnos_caja")
public class TurnoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idTurno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(updatable = false)
    private LocalDateTime fechaApertura = LocalDateTime.now();

    private LocalDateTime fechaCierre;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal montoInicial = BigDecimal.ZERO;

    @Column(precision = 38, scale = 2)
    private BigDecimal montoCalculado = BigDecimal.ZERO;

    @Column(precision = 38, scale = 2)
    private BigDecimal montoReal;

    @Column(precision = 38, scale = 2)
    private BigDecimal diferencia;

    @Column(length = 20)
    private String estado = "ABIERTO";

    public TurnoCaja() {}

    // ================= GETTERS Y SETTERS =================

    public UUID getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(UUID idTurno) {
        this.idTurno = idTurno;
    }

    public Caja getCaja() {
        return caja;
    }

    public void setCaja(Caja caja) {
        this.caja = caja;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public BigDecimal getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(BigDecimal montoInicial) {
        this.montoInicial = montoInicial;
    }

    public BigDecimal getMontoCalculado() {
        return montoCalculado;
    }

    public void setMontoCalculado(BigDecimal montoCalculado) {
        this.montoCalculado = montoCalculado;
    }

    public BigDecimal getMontoReal() {
        return montoReal;
    }

    public void setMontoReal(BigDecimal montoReal) {
        this.montoReal = montoReal;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
