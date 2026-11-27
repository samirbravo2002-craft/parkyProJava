package com.example.ejercicio.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.ejercicio.domain.ComprobantePago;
import com.example.ejercicio.domain.Reserva;

public interface ComprobantePagoDao extends CrudRepository<ComprobantePago, Integer> {

    Optional<ComprobantePago> findFirstByReservaIdReservaOrderByIdComprobantePagoAsc(Integer idReserva);

    // Comprobantes de varias reservas a la vez (para el historial)
    List<ComprobantePago> findByReservaIn(Collection<Reserva> reservas);
}
