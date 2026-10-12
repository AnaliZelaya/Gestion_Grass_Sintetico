package com.unas.grassbackend.service;

import com.unas.grassbackend.entity.Rol;
import java.util.List;

public interface RolService {
    List<Rol> listarTodos();
    Rol guardar(Rol rol);
    Rol buscarPorId(Integer id);
    void eliminar(Integer id);
}