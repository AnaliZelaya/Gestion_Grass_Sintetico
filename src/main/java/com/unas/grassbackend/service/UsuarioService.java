package com.unas.grassbackend.service;

import com.unas.grassbackend.entity.Usuario;
import java.util.List;
import java.util.UUID;

public interface UsuarioService {
    
    Usuario registrarUsuario(Usuario usuario);
    List<Usuario> listarTodos();
    
    // NUEVAS OPERACIONES:
    Usuario actualizarUsuario(UUID id, Usuario usuarioActualizado);
    void eliminarUsuario(UUID id);
    
}