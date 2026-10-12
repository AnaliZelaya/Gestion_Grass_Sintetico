package com.unas.grassbackend.service;

import com.unas.grassbackend.entity.Cancha;
import com.unas.grassbackend.repository.CanchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CanchaServiceImpl implements CanchaService {

    @Autowired
    private CanchaRepository canchaRepository;

    @Override
    public List<Cancha> listarTodas() {
        return canchaRepository.findAll();
    }

    @Override
    public List<Cancha> listarDisponibles() {
        return canchaRepository.findByEstado("Disponible");
    }
    
    @Override
    public List<Cancha> buscarDisponibles(LocalDateTime inicio, LocalDateTime fin) {
        return canchaRepository.buscarCanchasLibres(inicio, fin);
    }

    @Override
    public Cancha guardar(Cancha cancha) {
        return canchaRepository.save(cancha);
    }

    @Override
    public Cancha actualizar(UUID id, Cancha canchaActualizada) {
        return canchaRepository.findById(id).map(cancha -> {
            cancha.setNombre(canchaActualizada.getNombre());
            
            // AÑADIDO: Actualización del campo deporte
            cancha.setDeporte(canchaActualizada.getDeporte());
            
            cancha.setTipoGrass(canchaActualizada.getTipoGrass());
            cancha.setMedidas(canchaActualizada.getMedidas());
            cancha.setPrecioDia(canchaActualizada.getPrecioDia());
            cancha.setPrecioNoche(canchaActualizada.getPrecioNoche());
            cancha.setEstado(canchaActualizada.getEstado());
            
            // LÓGICA DE LA IMAGEN: Solo se reemplaza si el controlador subió una nueva
            if (canchaActualizada.getUrlImagen() != null) {
                cancha.setUrlImagen(canchaActualizada.getUrlImagen());
            }

            // LÓGICA DE LA SEDE: Guarda la asignación de la sede si se envía
            if (canchaActualizada.getSede() != null) {
                cancha.setSede(canchaActualizada.getSede());
            }

            return canchaRepository.save(cancha);
        }).orElseThrow(() -> new RuntimeException("Cancha no encontrada"));
    }

    @Override
    public void eliminar(UUID id) {
        canchaRepository.deleteById(id);
    }
}