package com.unas.grassbackend.service;

import com.unas.grassbackend.entity.Reserva;
import com.unas.grassbackend.entity.Venta;
import com.unas.grassbackend.repository.ReservaRepository;
import com.unas.grassbackend.repository.VentaRepository;
import com.unas.grassbackend.repository.CanchaRepository;
import com.unas.grassbackend.security.SedeContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private VentaRepository ventaRepository;
    @Autowired private CanchaRepository canchaRepository;

    public Map<String, Object> obtenerResumenDashboard(String filtro) {
        String sedeActiva = SedeContextHolder.getSede();
        List<Reserva> reservasTodas;
        List<Venta> ventasTodas;

        // 1. Obtener datos por sede
        if (sedeActiva == null || "GLOBAL".equals(sedeActiva)) {
            reservasTodas = reservaRepository.findAll();
            ventasTodas = ventaRepository.findAll();
        } else {
            try {
                Integer idSede = Integer.parseInt(sedeActiva);
                reservasTodas = reservaRepository.findBySedeId(idSede);
                ventasTodas = ventaRepository.findAll(); 
            } catch (NumberFormatException e) {
                reservasTodas = reservaRepository.findAll();
                ventasTodas = ventaRepository.findAll();
            }
        }

        // 2. Configurar el rango de fechas según el filtro seleccionado
        LocalDate hoy = LocalDate.now();
        LocalDateTime fechaInicio;
        LocalDateTime fechaFin = hoy.atTime(23, 59, 59);

        if (filtro == null) filtro = "Histórico Total";
        String filtroNormalizado = filtro.toLowerCase().trim();

        // 🟢 CORRECCIÓN: Agregamos los términos que envía el React (diaria, semanal, etc.)
        switch (filtroNormalizado) {
            case "diaria":
            case "hoy":
                fechaInicio = hoy.atStartOfDay();
                break;
            case "semanal":
            case "esta semana":
                fechaInicio = hoy.with(DayOfWeek.MONDAY).atStartOfDay();
                break;
            case "mensual":
            case "este mes":
                fechaInicio = hoy.withDayOfMonth(1).atStartOfDay();
                break;
            case "anual":
            case "este año":
            case "este ano":
                fechaInicio = hoy.withDayOfYear(1).atStartOfDay();
                break;
            case "histórico total":
            case "historico total":
            case "histórico":
            case "historico":
            default:
                fechaInicio = LocalDateTime.of(2000, 1, 1, 0, 0); 
                break;
        }

        // 3. Filtrar las reservas que caigan dentro del rango de tiempo establecido
        LocalDateTime finalFechaInicio = fechaInicio;
        List<Reserva> reservasFiltradas = reservasTodas.stream()
                .filter(r -> r.getHoraInicio() != null && 
                             !r.getHoraInicio().isBefore(finalFechaInicio) && 
                             !r.getHoraInicio().isAfter(fechaFin))
                .collect(Collectors.toList());

        List<Venta> ventasFiltradas = ventasTodas.stream()
                .filter(v -> v.getFechaVenta() != null && 
                             !v.getFechaVenta().isBefore(finalFechaInicio) && 
                             !v.getFechaVenta().isAfter(fechaFin))
                .collect(Collectors.toList());

        // 4. Variables acumuladoras para las tarjetas del Dashboard
        BigDecimal ingresosPorReservas = BigDecimal.ZERO;
        int completadasCount = 0;
        int canceladasCount = 0;

        for (Reserva r : reservasFiltradas) {
            String estado = r.getEstado() != null ? r.getEstado().toUpperCase() : "";

            boolean esAprobada = estado.equals("CONFIRMADA") || 
                                 estado.equals("EN CURSO") || 
                                 estado.equals("FINALIZADA") || 
                                 estado.equals("PAGADA");

            if (esAprobada) {
                completadasCount++;
                BigDecimal montoIngresado = BigDecimal.ZERO;
                if (r.getAdelanto() != null && r.getAdelanto().compareTo(BigDecimal.ZERO) > 0) {
                    montoIngresado = r.getAdelanto();
                } else if (r.getTotal() != null) {
                    montoIngresado = r.getTotal();
                }
                ingresosPorReservas = ingresosPorReservas.add(montoIngresado);
            }

            if (estado.equals("CANCELADA")) {
                canceladasCount++;
            }
        }

        // Acumuladores de Tienda
        BigDecimal ingresosPorTienda = BigDecimal.ZERO;
        int ventasRealizadas = ventasFiltradas.size();
        for (Venta v : ventasFiltradas) {
            if (v.getTotal() > 0) {
                ingresosPorTienda = ingresosPorTienda.add(BigDecimal.valueOf(v.getTotal()));
            }
        }

        // Suma Total
        BigDecimal ingresosTotales = ingresosPorReservas.add(ingresosPorTienda);

        // 5. Armamos el JSON
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("canchasActivas", canchaRepository.count());
        respuesta.put("reservasFiltro", completadasCount + canceladasCount);
        respuesta.put("ingresosFiltro", ingresosTotales);
        respuesta.put("canceladasFiltro", canceladasCount);
        
        respuesta.put("ingresosReservas", ingresosPorReservas);
        respuesta.put("partidosCobrados", completadasCount);
        
        respuesta.put("ingresosTienda", ingresosPorTienda);
        respuesta.put("ventasRealizadas", ventasRealizadas);
        
        respuesta.put("estadoCompletadas", completadasCount);
        respuesta.put("estadoCanceladas", canceladasCount);

        respuesta.put("dineroEnCaja", ingresosTotales); 
        respuesta.put("reservasHoy", completadasCount);
        respuesta.put("canceladas", canceladasCount);

        return respuesta;
   }
}