package com.example.ejercicio.service;

import java.util.List;

import com.example.ejercicio.domain.Cliente;
import com.example.ejercicio.domain.Vehiculo;
import com.example.ejercicio.dto.ClienteForm;

public interface IClienteService {

    List<Cliente> listarClientes();

    Cliente buscarClientePorId(Integer noDocumento);

    // Lanza IllegalArgumentException con un mensaje listo para mostrar si algo está mal
    void guardarCliente(ClienteForm form);

    // Lanza IllegalStateException si el cliente tiene vehículos o reservas
    void eliminarCliente(Integer noDocumento);

    List<Vehiculo> vehiculosDelCliente(Integer noDocumento);
}
