package com.unas.grassbackend.repository;

import com.unas.grassbackend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);
    
    // NUEVO: Listar solo los activos
    List<Usuario> findByEstado(String estado);
}