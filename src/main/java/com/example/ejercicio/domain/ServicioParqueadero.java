package com.example.ejercicio.domain;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ENTIDAD ServicioParqueadero: tabla "servicio_parqueadero".
 * tipo_cobro solo admite 'HORA', 'DIA' o 'MES' (restricción CHECK del script).
 * El costo es lo que se cobra al generar la reserva.
 */
@Data
@Entity
@Table(name = "servicio_parqueadero")
public class ServicioParqueadero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio_parqueadero")
    private Integer idServicioParqueadero;

    @Column(name = "nombre_servicio_parqueadero", nullable = false, length = 30)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_vehiculo")
    private TipoVehiculo tipoVehiculo;

    @Column(name = "descripcion_servicio_parqueadero", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "tipo_cobro", nullable = false, length = 10)
    private String tipoCobro;

    @Column(name = "costo_servicio_parqueadero", nullable = false)
    private Double costo;

    @Column(name = "estado_servicio_parqueadero")
    private Boolean estado = true;

    public boolean isActivo() {
        return !Boolean.FALSE.equals(estado);
    }

    public String getTipoCobroTexto() {
        if ("HORA".equals(tipoCobro)) return "Por hora";
        if ("DIA".equals(tipoCobro)) return "Por día";
        if ("MES".equals(tipoCobro)) return "Por mes";
        return tipoCobro == null ? "—" : tipoCobro;
    }
}
