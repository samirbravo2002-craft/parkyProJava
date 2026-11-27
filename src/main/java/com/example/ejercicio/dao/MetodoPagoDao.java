package com.example.ejercicio.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.ejercicio.domain.MetodoPago;

public interface MetodoPagoDao extends CrudRepository<MetodoPago, Integer> {

    List<MetodoPago> findAllByOrderByIdMetodoPagoAsc();
}
