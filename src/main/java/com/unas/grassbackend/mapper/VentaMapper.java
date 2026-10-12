package com.unas.grassbackend.mapper;

import com.unas.grassbackend.dto.DetalleVentaDTO;
import com.unas.grassbackend.dto.VentaResponseDTO;
import com.unas.grassbackend.entity.DetalleVenta;
import com.unas.grassbackend.entity.Venta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VentaMapper {

    public VentaResponseDTO toDTO(Venta venta) {
        if (venta == null) return null;

        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setIdVenta(venta.getIdVenta());
        dto.setFechaVenta(venta.getFechaVenta());
        dto.setTotal(venta.getTotal());
        dto.setDocumentoCliente(venta.getDocumentoCliente());
        dto.setMetodoPago(venta.getMetodoPago());
        dto.setNombreCliente(venta.getNombreCliente());
        dto.setTipoComprobante(venta.getTipoComprobante());

        if (venta.getDetalles() != null) {
            List<DetalleVentaDTO> detallesDTO = venta.getDetalles().stream().map(this::detalleToDTO).collect(Collectors.toList());
            dto.setDetalles(detallesDTO);
        }

        return dto;
    }

    private DetalleVentaDTO detalleToDTO(DetalleVenta detalle) {
        DetalleVentaDTO dto = new DetalleVentaDTO();
        dto.setIdDetalle(detalle.getIdDetalle());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        
        if (detalle.getProducto() != null) {
            dto.setNombreProducto(detalle.getProducto().getNombre());
        }
        return dto;
    }

    public List<VentaResponseDTO> toDTOList(List<Venta> ventas) {
        return ventas.stream().map(this::toDTO).collect(Collectors.toList());
    }
}