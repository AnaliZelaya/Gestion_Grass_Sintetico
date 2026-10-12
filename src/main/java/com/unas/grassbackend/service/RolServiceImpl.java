package com.unas.grassbackend.service;

import com.unas.grassbackend.entity.Rol;
import com.unas.grassbackend.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    @Override
    public Rol guardar(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public Rol buscarPorId(Integer id) {
        return rolRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Integer id) {
        rolRepository.deleteById(id);
    }
}