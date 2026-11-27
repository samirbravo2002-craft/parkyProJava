package com.example.ejercicio.service;

import java.util.List;

import com.example.ejercicio.domain.MetodoPago;
import com.example.ejercicio.domain.ServicioLavado;
import com.example.ejercicio.domain.ServicioParqueadero;
import com.example.ejercicio.domain.TipoVehiculo;

/**
 * Catálogo del parqueadero: tipos de vehículo, servicios de parqueadero, servicios de lavado
 * y métodos de pago.
 */
public interface ICatalogoService {

    String CAT_PARQUEADERO = "PARQUEADERO";
    String CAT_LAVADO = "LAVADO";
    List<String> TIPOS_COBRO = List.of("HORA", "DIA", "MES");

    // ---------- Tipos de vehículo ----------
    List<TipoVehiculo> listarTipos();

    List<TipoVehiculo> listarTiposActivos();

    TipoVehiculo buscarTipo(Integer id);

    TipoVehiculo buscarTipoPorNombre(String nombre);

    void guardarTipo(TipoVehiculo tipo);

    // Lanza IllegalStateException si el tipo está en uso
    void eliminarTipo(Integer id);

    // Cupos del parqueadero para ese tipo (se leen de application.properties)
    int capacidad(TipoVehiculo tipo);

    // ---------- Servicios de parqueadero ----------
    List<ServicioParqueadero> listarServiciosParqueadero();

    List<ServicioParqueadero> listarServiciosParqueaderoActivos();

    ServicioParqueadero buscarServicioParqueadero(Integer id);

    void guardarServicioParqueadero(ServicioParqueadero servicio);

    // true = se eliminó ; false = tenía reservas y solo se desactivó
    boolean eliminarServicioParqueadero(Integer id);

    // ---------- Servicios de lavado ----------
    List<ServicioLavado> listarServiciosLavado();

    List<ServicioLavado> listarServiciosLavadoActivos();

    ServicioLavado buscarServicioLavado(Integer id);

    void guardarServicioLavado(ServicioLavado servicio);

    boolean eliminarServicioLavado(Integer id);

    // ---------- Métodos de pago ----------
    List<MetodoPago> listarMetodosPagoActivos();
}
