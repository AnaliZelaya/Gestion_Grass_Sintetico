package com.unas.grassbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ventas")
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idVenta;

    private LocalDateTime fechaVenta;
    private Double total;

    private String tipoComprobante;
    private String documentoCliente;
    private String nombreCliente;
    
    // NUEVO: GUARDARÁ YAPE, PLIN, EFECTIVO O TARJETA
    private String metodoPago; 

    // AQUÍ ESTÁ LA SOLUCIÓN: Conectamos la Venta a una Sede específica
    @ManyToOne
    @JoinColumn(name = "id_sede")
    private Sede sede;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;

    // --- GETTERS Y SETTERS ---
    public UUID getIdVenta() { return idVenta; }
    public void setIdVenta(UUID idVenta) { this.idVenta = idVenta; }

    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public String getDocumentoCliente() { return documentoCliente; }
    public void setDocumentoCliente(String documentoCliente) { this.documentoCliente = documentoCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    // GETTER Y SETTER DE LA SEDE
    public Sede getSede() { return sede; }
    public void setSede(Sede sede) { this.sede = sede; }
}