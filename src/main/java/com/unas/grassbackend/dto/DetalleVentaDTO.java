package com.unas.grassbackend.dto;

import java.util.UUID;

public class DetalleVentaDTO {
    private UUID idDetalle;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private String nombreProducto; // 🟢 Solo exponemos el nombre, no todo el objeto Producto

    public DetalleVentaDTO() {}

    // Getters y Setters
    public UUID getIdDetalle() { return idDetalle; }
    public void setIdDetalle(UUID idDetalle) { this.idDetalle = idDetalle; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
}
