package com.unas.grassbackend.controller;

import com.unas.grassbackend.entity.Cancha;
import com.unas.grassbackend.service.CanchaService;
import com.unas.grassbackend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/canchas")
public class CanchaController {

    @Autowired
    private CanchaService canchaService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public List<Cancha> listarTodas() {
        return canchaService.listarTodas();
    }

    @GetMapping("/disponibles")
    public List<Cancha> listarDisponibles() {
        return canchaService.listarDisponibles();
    }
    
    @GetMapping("/buscar")
    public List<Cancha> buscarCanchas(
            @RequestParam("inicio") LocalDateTime inicio,
            @RequestParam("fin") LocalDateTime fin) {
        return canchaService.buscarDisponibles(inicio, fin);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Cancha crear(
            @RequestPart("cancha") Cancha cancha,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) throws IOException {
        
        if (imagen != null && !imagen.isEmpty()) {
            String urlImagen = cloudinaryService.uploadImage(imagen);
            cancha.setUrlImagen(urlImagen);
        }
        
        return canchaService.guardar(cancha);
    }

    // 🟢 Usamos @PostMapping en lugar de @PutMapping para que Tomcat no bloquee la foto
    @PostMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Cancha actualizar(
            @PathVariable UUID id, 
            @RequestPart("cancha") Cancha cancha,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) throws IOException {
        
        // Solo actualizamos la URL en Cloudinary si de verdad subieron una foto nueva
        if (imagen != null && !imagen.isEmpty()) {
            String urlImagen = cloudinaryService.uploadImage(imagen);
            cancha.setUrlImagen(urlImagen);
        }
        
        return canchaService.actualizar(id, cancha);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable UUID id) {
        canchaService.eliminar(id);
        return "Cancha eliminada correctamente";
    }
}