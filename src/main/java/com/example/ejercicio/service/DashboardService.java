package com.example.ejercicio.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ejercicio.dao.ReservaDao;
import com.example.ejercicio.domain.Reserva;
import com.example.ejercicio.domain.TipoVehiculo;
import com.example.ejercicio.dto.DashboardResumen;
import com.example.ejercicio.dto.DashboardResumen.CupoTipo;
import com.example.ejercicio.util.Tiempo;

/**
 * Calcula los números del dashboard.
 * Un cupo está ocupado mientras la reserva esté en estado 1 (en el parqueadero).
 * Al registrar la salida (estado 2) el cupo se libera.
 */
@Service
public class DashboardService implements IDashboardService {

    @Autowired
    private ICatalogoService catalogoService;

    @Autowired
    private ReservaDao reservaDao;

    @Override
    @Transactional(readOnly = true)
    public DashboardResumen obtener() {
        LocalDate hoy = Tiempo.ahora().toLocalDate();

        List<CupoTipo> lista = new ArrayList<>();
        int totalCap = 0;
        int totalOcup = 0;

        for (TipoVehiculo t : catalogoService.listarTiposActivos()) {
            int capacidad = catalogoService.capacidad(t);
            int ocupados = (int) reservaDao.contarPorTipoYEstado(t.getIdTipoVehiculo(), Reserva.ACTIVA);
            int disponibles = Math.max(0, capacidad - ocupados);
            int porcentaje = capacidad == 0 ? 0 : Math.min(100, ocupados * 100 / capacidad);

            lista.add(new CupoTipo(t.getNombre(), capacidad, ocupados, disponibles, porcentaje));
            totalCap += capacidad;
            totalOcup += ocupados;
        }

        Double suma = reservaDao.sumarIngresos(hoy, Reserva.ANULADA);
        BigDecimal ingresos = suma == null ? BigDecimal.ZERO : BigDecimal.valueOf(suma);
        long reservasHoy = reservaDao.contarReservas(hoy, Reserva.ANULADA);

        return new DashboardResumen(lista, totalCap, totalOcup,
                Math.max(0, totalCap - totalOcup), ingresos, reservasHoy);
    }
}
