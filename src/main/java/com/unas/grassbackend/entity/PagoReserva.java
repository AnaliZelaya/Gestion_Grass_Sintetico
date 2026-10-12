package com.unas.grassbackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagos_reserva")
public class PagoReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_movimiento", nullable = false)
    private MovimientoCaja movimiento;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 50)
    private String tipoPago; 

    @Column(updatable = false)
    private LocalDateTime fechaPago = LocalDateTime.now();

    public PagoReserva() {}

    // ================= GETTERS Y SETTERS =================

    public UUID getIdPago() {
        return idPago;
    }

    public void setIdPago(UUID idPago) {
        this.idPago = idPago;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public MovimientoCaja getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoCaja movimiento) {
        this.movimiento = movimiento;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
}