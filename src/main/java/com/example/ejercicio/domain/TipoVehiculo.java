package com.example.ejercicio.domain;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ENTIDAD TipoVehiculo: tabla "tipo_vehiculo" (Carro, Moto...).
 * La tabla no tiene columna de capacidad: los cupos se configuran en application.properties
 * (parqueadero.capacidad.carro=50, parqueadero.capacidad.moto=30). Ver CatalogoService.capacidad().
 */
@Data
@Entity
@Table(name = "tipo_vehiculo")
public class TipoVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_vehiculo")
    private Integer idTipoVehiculo;

    @Column(name = "nombre_tipo_vehiculo", length = 20)
    private String nombre;

    // Así se llama la columna en el script SQL
    @Column(name = "estado_vehiculo")
    private Boolean estado = true;

    public boolean isActivo() {
        return !Boolean.FALSE.equals(estado);
    }
}
