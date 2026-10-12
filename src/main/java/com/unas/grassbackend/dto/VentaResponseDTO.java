package com.unas.grassbackend.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class VentaResponseDTO {
    private UUID idVenta;
    private LocalDateTime fechaVenta;
    private double total;
    private String documentoCliente;
    private String metodoPago;
    private String nombreCliente;
    private String tipoComprobante;
    
    private List<DetalleVentaDTO> detalles; // 🟢 Lista de los productos vendidos limpios

    public VentaResponseDTO() {}

    // Getters y Setters
    public UUID getIdVenta() { return idVenta; }
    public void setIdVenta(UUID idVenta) { this.idVenta = idVenta; }

    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getDocumentoCliente() { return documentoCliente; }
    public void setDocumentoCliente(String documentoCliente) { this.documentoCliente = documentoCliente; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public List<DetalleVentaDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaDTO> detalles) { this.detalles = detalles; }
}