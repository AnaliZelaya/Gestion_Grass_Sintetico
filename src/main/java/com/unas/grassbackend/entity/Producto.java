package com.unas.grassbackend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

@Entity
@Table(name = "productos")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idProducto;

    @Column(unique = true)
    private String codigo; // Código de barras (el primero de la lista, como principal)
    
    private String nombre;
    private Double precio;
    private Integer stock;
    private String categoria;
    
    @Column(columnDefinition = "TEXT")
    private String codigosVariantes; 

    //NUEVO: Relación con la tabla Sedes
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Evita errores al serializar JSON
    private Sede sede;

    // --- GETTERS Y SETTERS ---
    public UUID getIdProducto() { return idProducto; }
    public void setIdProducto(UUID idProducto) { this.idProducto = idProducto; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getCodigosVariantes() { return codigosVariantes; }
    public void setCodigosVariantes(String codigosVariantes) { this.codigosVariantes = codigosVariantes; }

    public Sede getSede() { return sede; }
    public void setSede(Sede sede) { this.sede = sede; }
}