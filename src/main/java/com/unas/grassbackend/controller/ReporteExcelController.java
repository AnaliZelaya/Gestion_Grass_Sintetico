package com.unas.grassbackend.controller;

import com.unas.grassbackend.service.ReporteExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteExcelController {

    @Autowired
    private ReporteExcelService reporteExcelService;

    @PostMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam String tipo,
            @RequestBody Map<String, List<Map<String, Object>>> payloadDatos) {
        
        byte[] excelContent = reporteExcelService.generarExcel(tipo, payloadDatos);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "Reporte_" + tipo + ".xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }
}
