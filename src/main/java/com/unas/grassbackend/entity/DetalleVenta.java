package com.unas.grassbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_venta")
    @JsonIgnore 
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    // --- GETTERS Y SETTERS ---
    public UUID getIdDetalle() { return idDetalle; }
    public void setIdDetalle(UUID idDetalle) { this.idDetalle = idDetalle; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}