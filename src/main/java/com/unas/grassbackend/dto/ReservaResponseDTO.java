package com.unas.grassbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReservaResponseDTO {
    private UUID idReserva;
    private UUID idCancha;
    
    // NUEVO: Agregado para que el frontend pueda identificar al dueño de la reserva
    private UUID idUsuario; 
    
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private String estado;
    private BigDecimal total;
    private BigDecimal adelanto;
    private String nombreCliente; // Si es reserva manual
    private String telefonoCliente;
    
    // Datos planos de relaciones (Sin exponer entidades completas)
    private String nombreCancha;
    private String nombreUsuario; // El cliente de la App (solo el nombre, sin contraseñas)

    // LA SOLUCIÓN: Agregamos el campo plano para el nombre de la sede
    private String nombreSede;

    // NUEVO: Objeto anidado para que React pueda leer reserva.cancha.nombre y reserva.cancha.urlImagen
    private CanchaResumenDTO cancha;
    
    private String codigoOperacion;
    private LocalDateTime fechaReserva;

    // Constructor vacío
    public ReservaResponseDTO() {}

    // ==========================================
    // Getters y Setters
    // ==========================================

    public UUID getIdReserva() { return idReserva; }
    public void setIdReserva(UUID idReserva) { this.idReserva = idReserva; }

    public UUID getIdCancha() { return idCancha; }
    public void setIdCancha(UUID idCancha) { this.idCancha = idCancha; }

    public UUID getIdUsuario() { return idUsuario; }
    public void setIdUsuario(UUID idUsuario) { this.idUsuario = idUsuario; }

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

    public String getNombreCancha() { return nombreCancha; }
    public void setNombreCancha(String nombreCancha) { this.nombreCancha = nombreCancha; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    // 🟢 GETTER Y SETTER DE LA SEDE
    public String getNombreSede() { return nombreSede; }
    public void setNombreSede(String nombreSede) { this.nombreSede = nombreSede; }

    public CanchaResumenDTO getCancha() { return cancha; }
    public void setCancha(CanchaResumenDTO cancha) { this.cancha = cancha; }
    
    public String getCodigoOperacion() { return codigoOperacion; }
    public void setCodigoOperacion(String codigoOperacion) { this.codigoOperacion = codigoOperacion; }

    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }
    
    // ==========================================
    // NUEVO: Clase estática interna para la estructura de la Cancha
    // ==========================================
    public static class CanchaResumenDTO {
        private String nombre;
        private String urlImagen;

        // Constructores
        public CanchaResumenDTO() {}

        public CanchaResumenDTO(String nombre, String urlImagen) {
            this.nombre = nombre;
            this.urlImagen = urlImagen;
        }

        // Getters y Setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getUrlImagen() { return urlImagen; }
        public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
    }
}