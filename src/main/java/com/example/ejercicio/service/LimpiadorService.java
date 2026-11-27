package com.example.ejercicio.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ejercicio.dao.LimpiadorDao;
import com.example.ejercicio.domain.Limpiador;

@Service
public class LimpiadorService implements ILimpiadorService {

    @Autowired
    private LimpiadorDao limpiadorDao;

    @Override
    public List<Limpiador> listarLimpiadores() {
        return limpiadorDao.findAllByOrderByNombreAscApellidoAsc();
    }

    @Override
    public Limpiador buscarLimpiadorPorId(Integer id_limpiador) {
        return id_limpiador == null ? null : limpiadorDao.findById(id_limpiador).orElse(null);
    }

    @Override
    public void guardarLimpiador(Limpiador limpiador) {
        limpiador.setCorreo(limpiador.getCorreo().trim().toLowerCase());
        limpiadorDao.findFirstByCorreoIgnoreCase(limpiador.getCorreo()).ifPresent(otro -> {
            if (!Objects.equals(otro.getIdLimpiador(), limpiador.getIdLimpiador())) {
                throw new IllegalArgumentException("Ya hay un limpiador con ese correo.");
            }
        });
        if (limpiador.getEstado() == null) {
            limpiador.setEstado(false);
        }
        limpiadorDao.save(limpiador);
    }

    @Override
    public void eliminarLimpiador(Integer id_limpiador) {
        limpiadorDao.deleteById(id_limpiador);
    }
}
