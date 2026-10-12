package com.unas.grassbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "canchas")
public class Cancha {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cancha")
    private UUID idCancha;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // 🟢 NUEVO CAMPO AÑADIDO: Deporte (Fútbol, Vóley, Básquet)
    @Column(name = "deporte", length = 50)
    private String deporte;

    @Column(name = "tipo_grass", length = 50)
    private String tipoGrass;

    @Column(name = "medidas", length = 50)
    private String medidas;

    @Column(name = "precio_dia")
    private Double precioDia;

    @Column(name = "precio_noche")
    private Double precioNoche;

    @Column(name = "estado", length = 20)
    private String estado = "Disponible";

    @Column(name = "url_imagen", length = 500)
    private String urlImagen;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;
    
    // 🟢 CORRECCIÓN: Evita el bucle de serialización infinita
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sede")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "usuarios", "canchas"})
    private Sede sede;
    
    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public UUID getIdCancha() { return idCancha; }
    public void setIdCancha(UUID idCancha) { this.idCancha = idCancha; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDeporte() { return deporte; }
    public void setDeporte(String deporte) { this.deporte = deporte; }

    public String getTipoGrass() { return tipoGrass; }
    public void setTipoGrass(String tipoGrass) { this.tipoGrass = tipoGrass; }

    public String getMedidas() { return medidas; }
    public void setMedidas(String medidas) { this.medidas = medidas; }

    public Double getPrecioDia() { return precioDia; }
    public void setPrecioDia(Double precioDia) { this.precioDia = precioDia; }

    public Double getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(Double precioNoche) { this.precioNoche = precioNoche; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Sede getSede() { return sede; }
    public void setSede(Sede sede) { this.sede = sede; }
}