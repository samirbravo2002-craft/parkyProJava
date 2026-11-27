package com.example.ejercicio.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.ejercicio.domain.TipoVehiculo;

public interface TipoVehiculoDao extends CrudRepository<TipoVehiculo, Integer> {

    List<TipoVehiculo> findAllByOrderByIdTipoVehiculoAsc();

    Optional<TipoVehiculo> findFirstByNombreIgnoreCase(String nombre);
}
