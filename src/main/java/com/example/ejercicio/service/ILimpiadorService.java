package com.example.ejercicio.service;

import java.util.List;

import com.example.ejercicio.domain.Limpiador;

public interface ILimpiadorService {

    List<Limpiador> listarLimpiadores();

    Limpiador buscarLimpiadorPorId(Integer id_limpiador);

    // Lanza IllegalArgumentException si el correo ya está en uso
    void guardarLimpiador(Limpiador limpiador);

    void eliminarLimpiador(Integer id_limpiador);
}
