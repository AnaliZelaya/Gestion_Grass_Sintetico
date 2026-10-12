package com.unas.grassbackend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;

@Entity
@Table(name = "cajas")
public class Caja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idCaja;
    
 //Evita que el JSON intente cargar la sede y colapse la petición
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 20)
    private String estado = "ACTIVA";

    // Constructor vacío requerido por JPA
    public Caja() {}

    // ================= GETTERS Y SETTERS =================

    public UUID getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(UUID idCaja) {
        this.idCaja = idCaja;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}