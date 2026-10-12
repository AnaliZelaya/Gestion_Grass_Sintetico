package com.unas.grassbackend.controller;

import com.unas.grassbackend.entity.Rol;
import com.unas.grassbackend.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public List<Rol> listar() {
        return rolService.listarTodos();
    }

    @PostMapping
    public Rol crear(@RequestBody Rol rol) {
        return rolService.guardar(rol);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Integer id) {
        rolService.eliminar(id);
        return "Rol eliminado correctamente";
    }
}
