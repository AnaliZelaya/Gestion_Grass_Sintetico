package com.unas.grassbackend.controller;

import com.unas.grassbackend.dto.LoginRequest;
import com.unas.grassbackend.entity.Usuario;
import com.unas.grassbackend.repository.UsuarioRepository;
import com.unas.grassbackend.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID; // Asegúrate de tener esta importación

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        
        // 1. Buscamos el usuario en PostgreSQL por su email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());

        // 2. Si el usuario existe y la contraseña coincide
        if (usuarioOpt.isPresent() && passwordEncoder.matches(request.getPassword(), usuarioOpt.get().getPasswordHash())) {
            
            Usuario usuario = usuarioOpt.get();
            
            // 🟢 TRADUCTOR DE ROL ARREGLADO: Manejo de los 3 niveles
            String rolTexto;
            if (usuario.getIdRol() == 3) {
                rolTexto = "SUPER_ADMIN"; // El dueño
            } else if (usuario.getIdRol() == 1) {
                rolTexto = "ADMIN";       // Administrador de Sede
            } else {
                rolTexto = "CLIENTE";     // Clientes (Rol 2 o 4)
            }
            
            // 3. Respuesta exitosa para React
            Map<String, Object> response = new HashMap<>();
            String tokenReal = jwtUtil.generateToken(usuario.getEmail(), usuario.getIdRol());
            
            response.put("token", tokenReal);
            response.put("mensaje", "¡Bienvenido " + usuario.getNombre() + "!");
            response.put("rol", rolTexto); 
            response.put("idUsuario", usuario.getIdUsuario()); 
            
            // Enviamos los permisos guardados en la BD hacia React
            response.put("permisos", usuario.getPermisos()); 
            
            // LÓGICA MULTI-SEDE: Enviamos a React a qué sede pertenece este usuario
            if (usuario.getSede() != null) {
                response.put("idSede", usuario.getSede().getIdSede());
            } else {
                response.put("idSede", null); // En caso de que sea el Super Admin Global
            }
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Correo o contraseña incorrectos");
        }
    }

    // 🟢 NUEVO ENDPOINT: Maneja el inicio de sesión con Firebase / Google
    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String nombre = request.get("nombre");

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            Usuario usuario;

            if (usuarioOpt.isPresent()) {
                // El usuario ya existe, simplemente lo obtenemos
                usuario = usuarioOpt.get();
            } else {
                // Es un cliente nuevo, lo creamos automáticamente
                usuario = new Usuario();
                usuario.setNombre(nombre != null ? nombre : "Usuario Google");
                usuario.setEmail(email);
                
                // Le ponemos una contraseña imposible y aleatoria porque usará Google para entrar
                usuario.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
                
                usuario.setIdRol(4); // 4 = Rol Cliente
                usuario.setEstado("Activo");
                usuario.setSede(null); // Clientes son globales
                
                usuario = usuarioRepository.save(usuario);
            }

            // Mismo traductor de roles que en el login normal
            String rolTexto;
            if (usuario.getIdRol() == 3) {
                rolTexto = "SUPER_ADMIN";
            } else if (usuario.getIdRol() == 1) {
                rolTexto = "ADMIN";
            } else {
                rolTexto = "CLIENTE";
            }

            // Armamos la respuesta para React
            Map<String, Object> response = new HashMap<>();
            String tokenReal = jwtUtil.generateToken(usuario.getEmail(), usuario.getIdRol());
            
            response.put("token", tokenReal);
            response.put("mensaje", "¡Bienvenido " + usuario.getNombre() + "!");
            response.put("rol", rolTexto); 
            response.put("idUsuario", usuario.getIdUsuario()); 
            response.put("permisos", usuario.getPermisos()); 
            
            if (usuario.getSede() != null) {
                response.put("idSede", usuario.getSede().getIdSede());
            } else {
                response.put("idSede", null);
            }
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error procesando autenticación con Google: " + e.getMessage()));
        }
    }
    
    // REGISTRO PÚBLICO DE CLIENTES (Normal con contraseña)
    @PostMapping("/registro")
    public ResponseEntity<?> registrarCliente(@RequestBody Map<String, String> request) {
        try {
            // 1. Verificamos si el correo ya existe
            if (usuarioRepository.findByEmail(request.get("email")).isPresent()) {
                return ResponseEntity.badRequest().body("El correo ya está registrado.");
            }

            // 2. Creamos al nuevo cliente
            Usuario nuevoCliente = new Usuario();
            nuevoCliente.setNombre(request.get("nombre"));
            nuevoCliente.setEmail(request.get("email"));
            
            // Encriptamos la contraseña
            nuevoCliente.setPasswordHash(passwordEncoder.encode(request.get("password")));
            
            // 4 = Cliente (o 2 dependiendo de tu lógica)
            nuevoCliente.setIdRol(4); 
            nuevoCliente.setEstado("Activo");
            
            // Los clientes no tienen id_sede porque son globales
            nuevoCliente.setSede(null); 

            usuarioRepository.save(nuevoCliente);
            
            return ResponseEntity.ok("Cliente registrado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar: " + e.getMessage());
        }
    }

    // RUTA TEMPORAL PARA CREAR TU PRIMER USUARIO
    @GetMapping("/crear-admin")
    public ResponseEntity<?> crearAdminDePrueba() {
        if(usuarioRepository.findByEmail("admin@unas.edu.pe").isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe.");
        }

        Usuario admin = new Usuario();
        admin.setNombre("Administrador Principal");
        admin.setEmail("admin@unas.edu.pe");
        admin.setPasswordHash(passwordEncoder.encode("12345678"));
        admin.setIdRol(1); // 1 = Administrador
        
        // Asignamos todos los permisos por defecto a esta cuenta de prueba
        admin.setPermisos("{\"dashboard\":true,\"reservas\":true,\"canchas\":true,\"clientes\":true,\"pagos\":true,\"reportes\":true}");
        
        usuarioRepository.save(admin);
        
        return ResponseEntity.ok("Usuario administrador creado con éxito en PostgreSQL.");
    }
}