package com.unas.grassbackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_reserva")
    private UUID idReserva;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_cancha", nullable = false)
    private Cancha cancha;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    @Column(name = "hora_inicio", nullable = false)
    private LocalDateTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalDateTime horaFin;

    @Column(length = 20)
    private String estado = "Pendiente";

    @Column(name = "total")
    private BigDecimal total;

    // Adelanto (Seña) dejado por el cliente
    @Column(name = "adelanto")
    private BigDecimal adelanto;
    
    // Para guardar los datos reales del cliente cuando el Admin hace la reserva
    @Column(name = "nombre_cliente", length = 100)
    private String nombreCliente;

    @Column(name = "telefono_cliente", length = 20)
    private String telefonoCliente;

    // NUEVO CAMPO: Para rastrear pagos de Yape o Plin
    @Column(name = "codigo_operacion", length = 50)
    private String codigoOperacion;

    // --- Getters y Setters ---

    public UUID getIdReserva() { return idReserva; }
    public void setIdReserva(UUID idReserva) { this.idReserva = idReserva; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Cancha getCancha() { return cancha; }
    public void setCancha(Cancha cancha) { this.cancha = cancha; }

    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }

    public LocalDateTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalDateTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalDateTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalDateTime horaFin) { this.horaFin = horaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getAdelanto() { return adelanto; }
    public void setAdelanto(BigDecimal adelanto) { this.adelanto = adelanto; }
    
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    // GETTER Y SETTER DEL NUEVO CAMPO
    public String getCodigoOperacion() { return codigoOperacion; }
    public void setCodigoOperacion(String codigoOperacion) { this.codigoOperacion = codigoOperacion; }
}