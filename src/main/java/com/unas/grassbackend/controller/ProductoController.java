package com.unas.grassbackend.controller;

import com.unas.grassbackend.entity.Producto;
import com.unas.grassbackend.entity.Sede;
import com.unas.grassbackend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // 🟢 MEJORADO: Ahora filtra por el Header que envía React
    @GetMapping
    public List<Producto> listarProductos(@RequestHeader(value = "X-Sede-Activa", required = false) String idSedeActiva) {
        if (idSedeActiva != null && !idSedeActiva.equals("GLOBAL")) {
            try {
                return productoRepository.findBySede_IdSede(Integer.valueOf(idSedeActiva));
            } catch (NumberFormatException e) {
                return productoRepository.findAll();
            }
        }
        return productoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Map<String, Object> payload) {
        try {
            String codigoPrincipal = (String) payload.get("codigo");
            
            if (productoRepository.findByCodigo(codigoPrincipal).isPresent()) {
                return ResponseEntity.badRequest().body("El código de barras principal ya existe.");
            }

            Producto nuevo = new Producto();
            nuevo.setCodigo(codigoPrincipal);
            nuevo.setNombre((String) payload.get("nombre"));
            nuevo.setCategoria((String) payload.get("categoria"));
            nuevo.setPrecio(Double.valueOf(payload.get("precio").toString()));
            nuevo.setStock(Integer.valueOf(payload.get("stock").toString()));

            if (payload.containsKey("codigosVariantes")) {
                @SuppressWarnings("unchecked")
                List<String> listaCodigos = (List<String>) payload.get("codigosVariantes");
                ObjectMapper mapper = new ObjectMapper();
                nuevo.setCodigosVariantes(mapper.writeValueAsString(listaCodigos));
            }

            // 🟢 NUEVO: Capturar y vincular la Sede
            if (payload.containsKey("sede")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> sedePayload = (Map<String, Object>) payload.get("sede");
                
                if (sedePayload != null && sedePayload.get("idSede") != null) {
                    Sede sedeVinculada = new Sede();
                    sedeVinculada.setIdSede(Integer.valueOf(sedePayload.get("idSede").toString())); 
                    nuevo.setSede(sedeVinculada);
                }
            }

            return ResponseEntity.ok(productoRepository.save(nuevo));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al guardar el producto: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> aumentarStock(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        try {
            Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            @SuppressWarnings("unchecked")
            List<String> codigosNuevos = (List<String>) payload.get("nuevosCodigos");
            
            ObjectMapper mapper = new ObjectMapper();
            List<String> codigosExistentes = new ArrayList<>();
            
            if (producto.getCodigosVariantes() != null && !producto.getCodigosVariantes().isEmpty()) {
                codigosExistentes = mapper.readValue(producto.getCodigosVariantes(), new TypeReference<List<String>>(){});
            }
            
            codigosExistentes.addAll(codigosNuevos);
            
            producto.setCodigosVariantes(mapper.writeValueAsString(codigosExistentes));
            producto.setStock(producto.getStock() + codigosNuevos.size());
            
            return ResponseEntity.ok(productoRepository.save(producto));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el stock: " + e.getMessage());
        }
    }
}