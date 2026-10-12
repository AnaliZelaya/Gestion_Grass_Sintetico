package com.unas.grassbackend.repository;

import com.unas.grassbackend.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    // Método extra por si necesitamos buscar un rol por su nombre exacto (ej. "Administrador")
    Rol findByNombre(String nombre);
    
}