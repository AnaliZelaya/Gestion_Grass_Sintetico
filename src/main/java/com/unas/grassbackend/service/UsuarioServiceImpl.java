package com.unas.grassbackend.service;

import com.unas.grassbackend.entity.Usuario;
import com.unas.grassbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        usuario.setEstado("Activo"); // Por si acaso
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> listarTodos() {
        //  AJUSTE: Solo devolver los activos
        return usuarioRepository.findByEstado("Activo");
    }

    @Override
    public Usuario actualizarUsuario(UUID id, Usuario usuarioActualizado) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            usuarioExistente.setIdRol(usuarioActualizado.getIdRol());
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
            usuarioExistente.setEmail(usuarioActualizado.getEmail());
            usuarioExistente.setPasswordHash(usuarioActualizado.getPasswordHash());
            usuarioExistente.setEstado(usuarioActualizado.getEstado());
            
            return usuarioRepository.save(usuarioExistente);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public void eliminarUsuario(UUID id) {
        //  AJUSTE: Borrado Lógico
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setEstado("Inactivo");
            usuarioRepository.save(usuario);
        });
    }
}