package com.example.ejercicio.domain;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ENTIDAD Vehiculo: tabla "vehiculo". La llave es la placa.
 * Si al hacer una reserva la placa no existe, se crea aquí con el cliente y el tipo elegidos.
 */
@Data
@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @Column(name = "placa_vehiculo", length = 10)
    private String placa;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "no_documento_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_vehiculo")
    private TipoVehiculo tipoVehiculo;

    @Column(name = "color_vehiculo", length = 11)
    private String color;

    @Column(name = "marca_vehiculo", length = 20)
    private String marca;

    @Column(name = "modelo_vehiculo", length = 20)
    private String modelo;

    @Column(name = "estado_vehiculo")
    private Boolean estado = true;

    public boolean isActivo() {
        return !Boolean.FALSE.equals(estado);
    }
}
