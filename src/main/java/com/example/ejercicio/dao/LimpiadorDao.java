package com.example.ejercicio.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.ejercicio.domain.Limpiador;

public interface LimpiadorDao extends CrudRepository<Limpiador, Integer> {

    List<Limpiador> findAllByOrderByNombreAscApellidoAsc();

    Optional<Limpiador> findFirstByCorreoIgnoreCase(String correo);
}
