package com.unas.grassbackend.controller;

import com.unas.grassbackend.entity.Caja;
import com.unas.grassbackend.repository.CajaRepository;
import com.unas.grassbackend.security.SedeContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cajas")
public class CajaController {

    @Autowired
    private CajaRepository cajaRepository;

    @GetMapping
    public ResponseEntity<List<Caja>> listarCajas() {
        String sedeActiva = SedeContextHolder.getSede();

        if (sedeActiva == null || "GLOBAL".equals(sedeActiva)) {
            return ResponseEntity.ok(cajaRepository.findAll());
        } else {
            try {
                Long idSede = Long.parseLong(sedeActiva);
                // Retorna solo las cajas ACTIVAS de la sede en la que está el cajero
                return ResponseEntity.ok(cajaRepository.findBySede_IdSedeAndEstado(idSede, "ACTIVA"));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().build();
            }
        }
    }
}
