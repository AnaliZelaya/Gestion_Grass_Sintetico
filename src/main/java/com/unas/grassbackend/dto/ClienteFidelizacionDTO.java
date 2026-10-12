package com.unas.grassbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ClienteFidelizacionDTO {
    private UUID idUsuario;
    private String nombre;
    private String email;
    private String telefono;
    private long partidosJugados;
    private BigDecimal dineroGenerado;
    private LocalDateTime ultimaVisita;
    private String nivelFidelidad;

    public ClienteFidelizacionDTO() {}

    // Getters y Setters
    public UUID getIdUsuario() { return idUsuario; }
    public void setIdUsuario(UUID idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public long getPartidosJugados() { return partidosJugados; }
    public void setPartidosJugados(long partidosJugados) { this.partidosJugados = partidosJugados; }

    public BigDecimal getDineroGenerado() { return dineroGenerado; }
    public void setDineroGenerado(BigDecimal dineroGenerado) { this.dineroGenerado = dineroGenerado; }

    public LocalDateTime getUltimaVisita() { return ultimaVisita; }
    public void setUltimaVisita(LocalDateTime ultimaVisita) { this.ultimaVisita = ultimaVisita; }

    public String getNivelFidelidad() { return nivelFidelidad; }
    public void setNivelFidelidad(String nivelFidelidad) { this.nivelFidelidad = nivelFidelidad; }
}
