package com.example.ejercicio.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.ejercicio.domain.Cliente;

// La llave de cliente es el número de documento (Integer)
public interface ClienteDao extends CrudRepository<Cliente, Integer> {

    List<Cliente> findAllByOrderByUsuarioNombreAscUsuarioApellidoAsc();
}
