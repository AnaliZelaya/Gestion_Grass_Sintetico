package com.unas.grassbackend.controller;

import com.unas.grassbackend.entity.*;
import com.unas.grassbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seeder")
public class DataSeederController {

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private CanchaRepository canchaRepository;
    @Autowired private TurnoCajaRepository turnoCajaRepository;
    @Autowired private MovimientoCajaRepository movimientoCajaRepository;
    @Autowired private PagoReservaRepository pagoReservaRepository;
    @Autowired private VentaRepository ventaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private CajaRepository cajaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    private final String[] NOMBRES = {
        "Carlos", "Lucía", "Miguel", "Valeria", "Andrés", "Sofía", "Luis", "Ana", "Pedro", "Camila", 
        "Jorge", "Daniela", "Martín", "Elena", "Ricardo", "Juan", "Maria", "Jose", "Rosa", "David", 
        "Carmen", "Victor", "Julia", "Manuel", "Patricia", "Diego", "Paula", "Oscar", "Laura", "Eduardo", 
        "Andrea", "Fernando", "Monica", "Hugo", "Isabel", "Roberto", "Diana", "Raul", "Silvia", "Javier"
    };
    
    private final String[] APELLIDOS = {
        "Ruiz", "Fernández", "Torres", "Gómez", "Castro", "Vargas", "Mendoza", "Rojas", "Silva", "Paredes", 
        "Herrera", "Vega", "Salazar", "Cordero", "Pineda", "Perez", "Garcia", "Martinez", "Lopez", "Gonzalez", 
        "Rodriguez", "Sanchez", "Ramirez", "Cruz", "Flores", "Diaz", "Reyes", "Morales", "Ortiz", "Gutierrez", 
        "Chavez", "Ramos", "Alvarez", "Castillo", "Jimenez"
    };

    private final List<String> METODOS_PAGO = Arrays.asList("Efectivo", "Yape", "Plin", "Visa", "Mastercard");

    private String generarClienteAleatorio(Random random) {
        String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
        String apellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
        return nombre + " " + apellido;
    }

    // =========================================================================
    // 1. ENDPOINT: GENERAR RESERVAS Y TURNOS (CON HORARIOS REALISTAS Y USUARIO ADMIN)
    // =========================================================================
    @PostMapping("/generar-solo-reservas")
    @Transactional
    public ResponseEntity<?> generarSoloReservas() {
        pagoReservaRepository.deleteAll();
        movimientoCajaRepository.deleteAll();
        ventaRepository.deleteAll();
        reservaRepository.deleteAll();
        turnoCajaRepository.deleteAll();

        LocalDate inicio = LocalDate.of(2023, 1, 1);
        LocalDate fin = LocalDate.now(); 
        Random random = new Random();

        List<Caja> cajas = cajaRepository.findAll();
        List<Cancha> canchas = canchaRepository.findAll();
        
     //  ASIGNACIÓN EXACTA DEL USUARIO ADMINISTRADOR
        String idAdminBuscado = "6b2e90d0-2c06-4586-8b07-5a9470eebc27";
        Usuario admin = usuarioRepository.findAll().stream()
                .filter(u -> u.getIdUsuario() != null && u.getIdUsuario().toString().equals(idAdminBuscado))
                .findFirst()
                .orElseGet(() -> usuarioRepository.findAll().stream()
                        .filter(u -> u.getNombre() != null && u.getNombre().toLowerCase().contains("admin"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No se encontró el usuario Administrador en la BD.")));
        if (cajas.isEmpty() || canchas.isEmpty()) {
            return ResponseEntity.badRequest().body("Faltan cajas o canchas en la BD para simular.");
        }

        int metaReservasTotales = 6568;
        int metaCanceladas = 835;
        
        int reservasCreadas = 0;
        int canceladasCreadas = 0;

        List<LocalDate> listaFechas = new ArrayList<>();
        for (LocalDate fecha = inicio; !fecha.isAfter(fin); fecha = fecha.plusDays(1)) {
            listaFechas.add(fecha);
        }

        int reservasPorDiaBase = metaReservasTotales / listaFechas.size();
        int residuoReservas = metaReservasTotales % listaFechas.size();
        int intervaloCancelacion = metaReservasTotales / metaCanceladas;
        int contadorParaCancelar = 0;

        for (int d = 0; d < listaFechas.size(); d++) {
            LocalDate fecha = listaFechas.get(d);
            int cantidadHoy = reservasPorDiaBase + (d < residuoReservas ? 1 : 0);

            for (Caja caja : cajas) {
                if (cantidadHoy <= 0) break;

                // 🟢 TIEMPO REALISTA: Apertura entre 07:45 AM y 08:15 AM
                int minutosVariacionApertura = random.nextInt(31) - 15; 
                LocalTime horaAperturaReal = LocalTime.of(8, 0).plusMinutes(minutosVariacionApertura);

                TurnoCaja turno = new TurnoCaja();
                turno.setCaja(caja);
                turno.setUsuario(admin);
                turno.setFechaApertura(LocalDateTime.of(fecha, horaAperturaReal));
                turno.setMontoInicial(BigDecimal.valueOf(50.0)); 
                turno.setEstado("ABIERTO");
                turno = turnoCajaRepository.save(turno);

                BigDecimal totalIngresos = BigDecimal.ZERO;
                BigDecimal totalEgresos = BigDecimal.ZERO;

                int reservasParaEsteTurno = Math.min(cantidadHoy, random.nextInt(2) + 2);
                cantidadHoy -= reservasParaEsteTurno;

                for (int i = 0; i < reservasParaEsteTurno; i++) {
                    if (reservasCreadas >= metaReservasTotales) break;

                    Cancha cancha = canchas.get(random.nextInt(canchas.size()));
                    int horaInicio = random.nextInt(14) + 8; 
                    LocalDateTime fechaHoraInicio = LocalDateTime.of(fecha, LocalTime.of(horaInicio, 0));
                    LocalDateTime fechaHoraFin = fechaHoraInicio.plusHours(1);

                    String cliente = generarClienteAleatorio(random);
                    String metodo = METODOS_PAGO.get(random.nextInt(METODOS_PAGO.size()));
                    
                    double tarifa = (horaInicio >= 18) ? cancha.getPrecioNoche() : cancha.getPrecioDia();
                    BigDecimal totalReserva = BigDecimal.valueOf(tarifa);

                    boolean seraCancelada = false;
                    if (canceladasCreadas < metaCanceladas) {
                        contadorParaCancelar++;
                        if (contadorParaCancelar >= intervaloCancelacion || (metaCanceladas - canceladasCreadas) >= (metaReservasTotales - reservasCreadas)) {
                            seraCancelada = true;
                            canceladasCreadas++;
                            contadorParaCancelar = 0;
                        }
                    }

                    String estadoReserva;
                    if (seraCancelada) {
                        estadoReserva = "Cancelada";
                    } else if (!fecha.isAfter(LocalDate.now())) {
                        estadoReserva = "Finalizada"; 
                    } else {
                        estadoReserva = "Confirmada";
                    }

                    Reserva reserva = new Reserva();
                    reserva.setCancha(cancha);
                    reserva.setUsuario(admin);
                    reserva.setNombreCliente(cliente);
                    reserva.setTelefonoCliente("9" + (random.nextInt(90000000) + 10000000));
                    reserva.setHoraInicio(fechaHoraInicio);
                    reserva.setHoraFin(fechaHoraFin);
                    reserva.setTotal(totalReserva);
                    reserva.setAdelanto(totalReserva); 
                    reserva.setEstado(estadoReserva); 
                    // Simulamos que agendaron unos días antes, en un horario aleatorio
                    reserva.setFechaReserva(fechaHoraInicio.minusDays(random.nextInt(3)).minusHours(random.nextInt(12)));
                    reserva = reservaRepository.save(reserva);

                    MovimientoCaja movReserva = new MovimientoCaja();
                    movReserva.setTurno(turno);
                    movReserva.setTipoMovimiento("INGRESO");
                    movReserva.setConcepto("Pago Reserva - Cliente: " + cliente);
                    movReserva.setMetodoPago(metodo);
                    movReserva.setMonto(totalReserva);
                    // El pago de reserva se hace en minutos aleatorios antes del partido
                    movReserva.setFechaMovimiento(fechaHoraInicio.minusMinutes(random.nextInt(20) + 5));
                    movimientoCajaRepository.save(movReserva);

                    PagoReserva pago = new PagoReserva();
                    pago.setReserva(reserva);
                    pago.setMovimiento(movReserva);
                    pago.setMonto(totalReserva);
                    pago.setTipoPago(metodo);
                    pago.setFechaPago(movReserva.getFechaMovimiento());
                    pagoReservaRepository.save(pago);

                    totalIngresos = totalIngresos.add(totalReserva);

                    if (seraCancelada) {
                        MovimientoCaja movEgreso = new MovimientoCaja();
                        movEgreso.setTurno(turno);
                        movEgreso.setTipoMovimiento("EGRESO");
                        movEgreso.setConcepto("Devolución por Anulación - Cliente: " + cliente);
                        movEgreso.setMetodoPago("Efectivo");
                        movEgreso.setMonto(totalReserva);
                        movEgreso.setFechaMovimiento(fechaHoraInicio.minusMinutes(random.nextInt(10) + 1));
                        movimientoCajaRepository.save(movEgreso);

                        totalEgresos = totalEgresos.add(totalReserva);
                    }

                    reservasCreadas++;
                }

                // 🟢 TIEMPO REALISTA: Cierre de caja entre 10:45 PM y 11:20 PM
                int minutosVariacionCierre = random.nextInt(36) - 15; 
                LocalTime horaCierreReal = LocalTime.of(23, 0).plusMinutes(minutosVariacionCierre);

                BigDecimal montoCalculado = turno.getMontoInicial().add(totalIngresos).subtract(totalEgresos);
                turno.setMontoCalculado(montoCalculado);
                turno.setMontoReal(montoCalculado); 
                turno.setDiferencia(BigDecimal.ZERO);
                turno.setEstado("CERRADO");
                turno.setFechaCierre(LocalDateTime.of(fecha, horaCierreReal));
                turnoCajaRepository.save(turno);
            }

            if (reservasCreadas >= metaReservasTotales) break;
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Reservas generadas con éxito bajo el usuario Administrador y tiempos realistas.",
                "reservasTotales", reservasCreadas
        ));
    }

    // =========================================================================
    // 2. ENDPOINT: LIMPIAR VENTAS VIEJAS Y GENERAR LAS NUEVAS
    // =========================================================================
    @PostMapping("/generar-solo-ventas")
    @Transactional
    public ResponseEntity<?> generarSoloVentas() {
        List<MovimientoCaja> todosLosMovimientos = movimientoCajaRepository.findAll();
        List<MovimientoCaja> movimientosDeVentaViejos = new ArrayList<>();

        for (MovimientoCaja mov : todosLosMovimientos) {
            if (mov.getVenta() != null) {
                movimientosDeVentaViejos.add(mov);
                
                TurnoCaja turno = mov.getTurno();
                if (turno != null) {
                    BigDecimal montoRestar = mov.getMonto() != null ? mov.getMonto() : BigDecimal.ZERO;
                    BigDecimal calcActual = turno.getMontoCalculado() != null ? turno.getMontoCalculado() : BigDecimal.ZERO;
                    BigDecimal realActual = turno.getMontoReal() != null ? turno.getMontoReal() : BigDecimal.ZERO;
                    
                    turno.setMontoCalculado(calcActual.subtract(montoRestar));
                    turno.setMontoReal(realActual.subtract(montoRestar));
                    turnoCajaRepository.save(turno);
                }
            }
        }
        
        movimientoCajaRepository.deleteAll(movimientosDeVentaViejos);
        ventaRepository.deleteAll();

        List<Producto> productos = productoRepository.findAll();
        for(Producto p : productos) {
            String nom = p.getNombre().toLowerCase();
            if(nom.contains("oreo")) p.setNombre("Galleta Oreo");
            else if(nom.contains("pilsen")) p.setNombre("Cerveza Pilsen");
            else if(nom.contains("san juan")) p.setNombre("Cerveza San Juan");
            else if(nom.contains("cusqueña") || nom.contains("cusquena")) p.setNombre("Cerveza Cusqueña");
            else if(nom.contains("san mateo") || nom.contains("agua")) p.setNombre("Agua San Mateo");
            else if(nom.contains("sporade")) p.setNombre("Sporade");
            else if(nom.contains("gatorade")) p.setNombre("Gatorade");
            productoRepository.save(p);
        }

        Random random = new Random();
        List<TurnoCaja> turnosSede1 = turnoCajaRepository.findAll().stream()
                .filter(t -> t.getCaja() != null && 
                             t.getCaja().getSede() != null && 
                             t.getCaja().getSede().getIdSede() == 1)
                .collect(Collectors.toList());

        if (turnosSede1.isEmpty() || productos.isEmpty()) {
            return ResponseEntity.badRequest().body("No hay turnos o productos registrados en la Sede 1.");
        }

        int metaVentas = 8236;
        int ventasCreadas = 0;
        int boletasCreadas = 0;
        int facturasCreadas = 0;

        int ventasPorTurnoBase = metaVentas / turnosSede1.size();
        int residuoVentas = metaVentas % turnosSede1.size();

        for (int t = 0; t < turnosSede1.size(); t++) {
            TurnoCaja turno = turnosSede1.get(t);
            int ventasParaEsteTurno = ventasPorTurnoBase + (t < residuoVentas ? 1 : 0);

            BigDecimal ingresosExtra = BigDecimal.ZERO;

            for (int i = 0; i < ventasParaEsteTurno; i++) {
                if (ventasCreadas >= metaVentas) break;

                Producto prod = productos.get(random.nextInt(productos.size()));
                String nombreProd = prod.getNombre().toLowerCase();
                int cantidad;

                if (nombreProd.contains("cerveza")) {
                    cantidad = random.nextInt(5) + 6; 
                } else {
                    cantidad = random.nextInt(3) + 1; 
                }

                double totalVenta = prod.getPrecio() * cantidad;
                String metodo = METODOS_PAGO.get(random.nextInt(METODOS_PAGO.size()));
                String cliente = generarClienteAleatorio(random);

                String tipoComprobante = (random.nextDouble() > 0.8) ? "Factura" : "Boleta";
                String docCliente = tipoComprobante.equals("Factura") ? 
                                    "20" + (random.nextInt(900000000) + 100000000) : 
                                    String.valueOf(random.nextInt(90000000) + 10000000); 

                Venta venta = new Venta();
                venta.setSede(turno.getCaja().getSede());
                venta.setNombreCliente(cliente);
                venta.setMetodoPago(metodo);
                venta.setTipoComprobante(tipoComprobante);
                venta.setDocumentoCliente(docCliente);
                
                // 🟢 Generar fecha de venta realista: Dentro del horario de apertura del turno
                LocalDateTime inicioTurno = turno.getFechaApertura();
                LocalDateTime finTurno = turno.getFechaCierre();
                // Calcula un horario aleatorio entre la apertura y el cierre
                long minutosDuracion = java.time.Duration.between(inicioTurno, finTurno).toMinutes();
                LocalDateTime fechaRealVenta = inicioTurno.plusMinutes(random.nextInt((int) minutosDuracion));
                
                venta.setFechaVenta(fechaRealVenta); 
                venta.setTotal(totalVenta);
                
                DetalleVenta det = new DetalleVenta();
                det.setProducto(prod);
                det.setCantidad(cantidad);
                det.setPrecioUnitario(prod.getPrecio());
                det.setSubtotal(totalVenta);
                det.setVenta(venta);
                venta.setDetalles(Arrays.asList(det));
                
                venta = ventaRepository.save(venta);

                MovimientoCaja movVenta = new MovimientoCaja();
                movVenta.setTurno(turno);
                movVenta.setTipoMovimiento("INGRESO");
                movVenta.setConcepto("Venta Tienda: " + prod.getNombre() + " (" + tipoComprobante + ")");
                movVenta.setMetodoPago(metodo);
                movVenta.setMonto(BigDecimal.valueOf(totalVenta));
                movVenta.setFechaMovimiento(fechaRealVenta); // Usamos la misma fecha realista
                movVenta.setVenta(venta);
                movimientoCajaRepository.save(movVenta);

                ingresosExtra = ingresosExtra.add(BigDecimal.valueOf(totalVenta));
                ventasCreadas++;
                
                if(tipoComprobante.equals("Factura")) facturasCreadas++;
                else boletasCreadas++;
            }

            BigDecimal montoCalActual = turno.getMontoCalculado() != null ? turno.getMontoCalculado() : BigDecimal.ZERO;
            BigDecimal montoRealActual = turno.getMontoReal() != null ? turno.getMontoReal() : BigDecimal.ZERO;

            turno.setMontoCalculado(montoCalActual.add(ingresosExtra));
            turno.setMontoReal(montoRealActual.add(ingresosExtra));
            turnoCajaRepository.save(turno);

            if (ventasCreadas >= metaVentas) break;
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Ventas limpiadas y generadas con horarios realistas dentro del turno.",
                "ventasRealizadas", ventasCreadas,
                "boletasGeneradas", boletasCreadas,
                "facturasGeneradas", facturasCreadas
        ));
    }
}