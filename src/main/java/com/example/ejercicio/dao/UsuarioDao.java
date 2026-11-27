package com.example.ejercicio.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.ejercicio.domain.Usuario;

public interface UsuarioDao extends CrudRepository<Usuario, Integer> {

    // El login se hace con el correo (columna correo_usuario)
    Optional<Usuario> findFirstByCorreoIgnoreCase(String correo);
}
