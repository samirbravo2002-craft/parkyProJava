package com.example.ejercicio.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.ejercicio.domain.ServicioLavado;

public interface ServicioLavadoDao extends CrudRepository<ServicioLavado, Integer> {

    List<ServicioLavado> findAllByOrderByTipoVehiculoIdTipoVehiculoAscCostoAsc();

    boolean existsByTipoVehiculoIdTipoVehiculo(Integer idTipoVehiculo);
}
