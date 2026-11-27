package com.example.ejercicio.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.ejercicio.domain.Vehiculo;

// La llave de vehiculo es la placa (String)
public interface VehiculoDao extends CrudRepository<Vehiculo, String> {

    List<Vehiculo> findAllByOrderByPlacaAsc();

    List<Vehiculo> findByClienteNoDocumentoOrderByPlacaAsc(Integer noDocumento);

    boolean existsByClienteNoDocumento(Integer noDocumento);

    boolean existsByTipoVehiculoIdTipoVehiculo(Integer idTipoVehiculo);
}
