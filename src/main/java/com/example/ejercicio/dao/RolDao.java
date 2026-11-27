package com.example.ejercicio.dao;

import org.springframework.data.repository.CrudRepository;

import com.example.ejercicio.domain.Rol;

public interface RolDao extends CrudRepository<Rol, Integer> {
}
