package com.example.ejercicio.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.ejercicio.domain.Reserva;

public interface ReservaDao extends CrudRepository<Reserva, Integer> {

    List<Reserva> findAllByOrderByIdReservaDesc();

    // Búsqueda por placa (para el historial)
    List<Reserva> findByVehiculoPlacaContainingIgnoreCaseOrderByIdReservaDesc(String placa);

    // ¿Esa placa ya está dentro del parqueadero?
    boolean existsByVehiculoPlacaAndEstado(String placa, Short estado);

    boolean existsByClienteNoDocumento(Integer noDocumento);

    boolean existsByServicioParqueaderoIdServicioParqueadero(Integer id);

    boolean existsByServicioLavadoIdServicioLavado(Integer id);

    // Vehículos de un tipo que están ahora en el parqueadero (estado = 1)
    @Query("select count(r) from Reserva r where r.vehiculo.tipoVehiculo.idTipoVehiculo = :idTipo and r.estado = :estado")
    long contarPorTipoYEstado(@Param("idTipo") Integer idTipo, @Param("estado") Short estado);

    // Dinero de las reservas de un día (no cuenta las anuladas)
    @Query("select coalesce(sum(sp.costo + coalesce(sl.costo, 0.0)), 0.0) from Reserva r "
            + "join r.servicioParqueadero sp left join r.servicioLavado sl "
            + "where r.fecha = :fecha and r.estado <> :anulada")
    Double sumarIngresos(@Param("fecha") LocalDate fecha, @Param("anulada") Short anulada);

    @Query("select count(r) from Reserva r where r.fecha = :fecha and r.estado <> :anulada")
    long contarReservas(@Param("fecha") LocalDate fecha, @Param("anulada") Short anulada);
}
