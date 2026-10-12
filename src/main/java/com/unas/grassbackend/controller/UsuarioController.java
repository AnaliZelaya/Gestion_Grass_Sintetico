package com.unas.grassbackend.controller;

import com.unas.grassbackend.entity.Sede;
import com.unas.grassbackend.entity.Usuario;
import com.unas.grassbackend.repository.SedeRepository;
import com.unas.grassbackend.repository.UsuarioRepository;
import com.unas.grassbackend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController 
@RequestMapping("/api/usuarios") 
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService; 

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarTodos();
    }

    @PostMapping
    public Usuario registrarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }

    @PostMapping("/personal")
    public ResponseEntity<?> registrarPersonal(@RequestBody Map<String, Object> payload) {
        try {
            Usuario nuevo = new Usuario();
            nuevo.setNombre((String) payload.get("nombre"));
            nuevo.setEmail((String) payload.get("email"));
            
            String passwordPlana = (String) payload.get("password");
            nuevo.setPasswordHash(passwordEncoder.encode(passwordPlana));
            
            nuevo.setIdRol((Integer) payload.get("idRol"));
            nuevo.setEstado("Activo");

            if (payload.containsKey("permisos")) {
                nuevo.setPermisos((String) payload.get("permisos"));
            }
            
            Object idSedeObj = payload.get("idSede");
            if (idSedeObj != null && !idSedeObj.toString().isEmpty()) {
                Integer idSede = Integer.parseInt(idSedeObj.toString());
                Sede sede = sedeRepository.findById(idSede).orElse(null);
                nuevo.setSede(sede);
            }
            
            usuarioRepository.save(nuevo);
            return ResponseEntity.ok(Map.of("message", "Personal registrado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al registrar: " + e.getMessage()));
        }
    }
    
    @PutMapping("/personal/{id}")
    public ResponseEntity<?> actualizarPersonal(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        try {
            Usuario usuarioExistente = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuarioExistente.setNombre((String) payload.get("nombre"));
            usuarioExistente.setEmail((String) payload.get("email"));
            
            String nuevaPass = (String) payload.get("password");
            if (nuevaPass != null && !nuevaPass.trim().isEmpty()) {
                usuarioExistente.setPasswordHash(passwordEncoder.encode(nuevaPass));
            }

            usuarioExistente.setIdRol((Integer) payload.get("idRol"));

            if (payload.containsKey("permisos")) {
                usuarioExistente.setPermisos((String) payload.get("permisos"));
            }

            Object idSedeObj = payload.get("idSede");
            if (idSedeObj != null && !idSedeObj.toString().isEmpty()) {
                Integer idSede = Integer.parseInt(idSedeObj.toString());
                Sede sede = sedeRepository.findById(idSede).orElse(null);
                usuarioExistente.setSede(sede);
            } else {
                usuarioExistente.setSede(null); 
            }

            usuarioRepository.save(usuarioExistente);
            return ResponseEntity.ok(Map.of("message", "Personal actualizado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al actualizar: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable UUID id, @RequestBody Usuario usuario) {
        return usuarioService.actualizarUsuario(id, usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable UUID id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente"));
    }
}