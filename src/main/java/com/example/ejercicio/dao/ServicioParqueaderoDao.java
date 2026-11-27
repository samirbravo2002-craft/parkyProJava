package com.example.ejercicio.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.ejercicio.domain.ServicioParqueadero;

public interface ServicioParqueaderoDao extends CrudRepository<ServicioParqueadero, Integer> {

    List<ServicioParqueadero> findAllByOrderByTipoVehiculoIdTipoVehiculoAscCostoAsc();

    boolean existsByTipoVehiculoIdTipoVehiculo(Integer idTipoVehiculo);
}
