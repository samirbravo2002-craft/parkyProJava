package com.example.ejercicio.domain;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ENTIDAD ServicioLavado: tabla "servicio_lavado" (servicio complementario).
 */
@Data
@Entity
@Table(name = "servicio_lavado")
public class ServicioLavado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio_lavado")
    private Integer idServicioLavado;

    @Column(name = "nombre_servicio_lavado", nullable = false, length = 30)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_vehiculo")
    private TipoVehiculo tipoVehiculo;

    @Column(name = "descripcion_servicio_lavado", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "costo_servicio_lavado", nullable = false)
    private Double costo;

    @Column(name = "estado_servicio_lavado")
    private Boolean estado = true;

    public boolean isActivo() {
        return !Boolean.FALSE.equals(estado);
    }
}
