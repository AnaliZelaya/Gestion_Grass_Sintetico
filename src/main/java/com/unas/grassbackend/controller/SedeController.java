package com.unas.grassbackend.controller;

import com.unas.grassbackend.entity.Sede;
import com.unas.grassbackend.repository.SedeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    @Autowired
    private SedeRepository sedeRepository;

    @GetMapping
    public List<Sede> listarSedes() {
        // AJUSTE: Ahora solo lista las Activas
        return sedeRepository.findByEstado("Activo");
    }

    @PostMapping
    public ResponseEntity<Sede> crearSede(@RequestBody Sede sede) {
        sede.setEstado("Activo"); // Aseguramos que nazca activa
        return ResponseEntity.ok(sedeRepository.save(sede));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sede> actualizarSede(@PathVariable Integer id, @RequestBody Sede sedeDetalles) {
        return sedeRepository.findById(id)
                .map(sedeExistente -> {
                    sedeExistente.setNombre(sedeDetalles.getNombre());
                    sedeExistente.setCiudad(sedeDetalles.getCiudad());
                    sedeExistente.setDireccion(sedeDetalles.getDireccion());
                    sedeExistente.setTelefono(sedeDetalles.getTelefono());
                    
                    Sede sedeActualizada = sedeRepository.save(sedeExistente);
                    return ResponseEntity.ok(sedeActualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSede(@PathVariable Integer id) {
        // AJUSTE: Borrado Lógico
        return sedeRepository.findById(id)
                .map(sede -> {
                    sede.setEstado("Inactivo");
                    sedeRepository.save(sede);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}