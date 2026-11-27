package com.example.ejercicio.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Lo que muestra el dashboard fijo de arriba: cupos por tipo de vehículo,
 * totales y dinero ingresado hoy.
 */
@Data
@AllArgsConstructor
public class DashboardResumen {

    private List<CupoTipo> tipos;
    private int totalCapacidad;
    private int totalOcupados;
    private int totalDisponibles;
    private BigDecimal ingresosHoy;
    private long reservasHoy;

    @Data
    @AllArgsConstructor
    public static class CupoTipo {
        private String nombre;
        private int capacidad;
        private int ocupados;
        private int disponibles;
        private int porcentaje;
    }
}
