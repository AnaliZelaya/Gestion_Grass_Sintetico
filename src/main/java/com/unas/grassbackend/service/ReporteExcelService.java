package com.unas.grassbackend.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ReporteExcelService {

    public byte[] generarExcel(String tipo, Map<String, List<Map<String, Object>>> payloadDatos) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            if ("RESERVAS".equalsIgnoreCase(tipo) || "AMBOS".equalsIgnoreCase(tipo)) {
                crearHojaReservas(workbook, headerStyle, payloadDatos.get("reservas"));
            }
            if ("TIENDA".equalsIgnoreCase(tipo) || "AMBOS".equalsIgnoreCase(tipo)) {
                crearHojaTienda(workbook, headerStyle, payloadDatos.get("ventas"));
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }

    private void crearHojaReservas(Workbook workbook, CellStyle headerStyle, List<Map<String, Object>> reservas) {
        Sheet sheet = workbook.createSheet("Reservas");
        Row headerRow = sheet.createRow(0);

        String[] columnas = {
            "ID Reserva", "Estado", "Fecha Reserva", "Hora Inicio", "Hora Fin", 
            "Total (S/)", "Cancha", "Adelanto", "Cliente", "Teléfono", "Cod. Operación"
        };

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        if (reservas != null && !reservas.isEmpty()) {
            int rowIdx = 1;
            for (Map<String, Object> r : reservas) {
                Row row = sheet.createRow(rowIdx++);
                
                // Mapeo exacto basado en tu JSON
                row.createCell(0).setCellValue(obtenerCualquierValor(r, "idReserva"));
                row.createCell(1).setCellValue(obtenerCualquierValor(r, "estado"));
                
                // SI FECHA_RESERVA NO LLEGA, verifica tu DTO en el backend
                row.createCell(2).setCellValue(obtenerCualquierValor(r, "fechaReserva", "fecha_reserva"));
                
                row.createCell(3).setCellValue(obtenerCualquierValor(r, "horaInicio"));
                row.createCell(4).setCellValue(obtenerCualquierValor(r, "horaFin"));
                row.createCell(5).setCellValue(obtenerDouble(r, "total"));
                row.createCell(6).setCellValue(obtenerCualquierValor(r, "nombreCancha"));
                row.createCell(7).setCellValue(obtenerDouble(r, "adelanto"));
                row.createCell(8).setCellValue(obtenerCualquierValor(r, "nombreCliente"));
                row.createCell(9).setCellValue(obtenerCualquierValor(r, "telefonoCliente"));
                
                // Asegúrate que esta propiedad exista en tu entidad Reserva de Java
                row.createCell(10).setCellValue(obtenerCualquierValor(r, "codigoOperacion", "codigo_operacion"));
            }
        }
        for (int i = 0; i < columnas.length; i++) { sheet.autoSizeColumn(i); }
    }

    private String obtenerCualquierValor(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) return map.get(key).toString();
        }
        return "";
    }
    
    private double obtenerDouble(Map<String, Object> map, String... keys) {
        String val = obtenerCualquierValor(map, keys);
        if (val.isEmpty()) return 0.0;
        try { return Double.parseDouble(val); } catch (Exception e) { return 0.0; }
    }

    private void crearHojaTienda(Workbook workbook, CellStyle headerStyle, List<Map<String, Object>> ventas) {
   
    }
}