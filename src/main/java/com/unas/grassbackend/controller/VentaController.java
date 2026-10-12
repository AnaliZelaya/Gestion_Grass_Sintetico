package com.unas.grassbackend.controller;

import com.unas.grassbackend.dto.VentaResponseDTO;
import com.unas.grassbackend.entity.Venta;
import com.unas.grassbackend.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // Totalmente protegido por el interceptor de seguridad y mapeado al DTO
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    // La lógica pesada ahora vive en el Service
    @PostMapping
    public ResponseEntity<Venta> registrarVenta(@RequestBody Venta venta) {
        return ResponseEntity.ok(ventaService.realizarVenta(venta));
    }
}