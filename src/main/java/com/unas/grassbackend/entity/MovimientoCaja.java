package com.unas.grassbackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movimientos_caja")
public class MovimientoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno", nullable = false)
    private TurnoCaja turno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    private Venta venta;

    @Column(nullable = false, length = 20)
    private String tipoMovimiento; 

    @Column(nullable = false, length = 255)
    private String concepto;

    @Column(nullable = false, length = 50)
    private String metodoPago;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal monto;

    @Column(updatable = false)
    private LocalDateTime fechaMovimiento = LocalDateTime.now();

    @Column(length = 20)
    private String codigoSeguridad;

    public MovimientoCaja() {}

    // ================= GETTERS Y SETTERS =================

    public UUID getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(UUID idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public TurnoCaja getTurno() {
        return turno;
    }

    public void setTurno(TurnoCaja turno) {
        this.turno = turno;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getCodigoSeguridad() {
        return codigoSeguridad;
    }

    public void setCodigoSeguridad(String codigoSeguridad) {
        this.codigoSeguridad = codigoSeguridad;
    }
}