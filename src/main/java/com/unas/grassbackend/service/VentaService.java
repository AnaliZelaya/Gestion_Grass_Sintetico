package com.unas.grassbackend.service;

import com.unas.grassbackend.dto.VentaResponseDTO;
import com.unas.grassbackend.entity.Venta;
import java.util.List;

public interface VentaService {
    List<VentaResponseDTO> listarTodas();
    Venta realizarVenta(Venta venta);
}