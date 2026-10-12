package com.unas.grassbackend.controller;

import com.unas.grassbackend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // 🟢 Recibe el filtro desde el frontend. Si no envían nada, por defecto carga el "Histórico Total"
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumen(
            @RequestParam(required = false, defaultValue = "Histórico Total") String filtro) {
        
        return ResponseEntity.ok(dashboardService.obtenerResumenDashboard(filtro));
    }
}