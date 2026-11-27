package com.example.ejercicio.service;

import java.util.List;

import com.example.ejercicio.domain.Reserva;
import com.example.ejercicio.domain.Vehiculo;
import com.example.ejercicio.dto.ReservaForm;

public interface IReservaService {

    List<Reserva> listar(String q);

    Reserva buscarPorId(Integer id);

    // Lanza IllegalArgumentException con un mensaje listo para mostrar si algo está mal
    Reserva crear(ReservaForm form, String correoUsuario);

    // Marca la reserva como finalizada (el vehículo salió) y libera el cupo
    void registrarSalida(Integer id);

    List<Vehiculo> listarVehiculos();
}
