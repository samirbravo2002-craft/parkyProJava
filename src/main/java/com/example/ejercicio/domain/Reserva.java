package com.example.ejercicio.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ENTIDAD Reserva: tabla "reserva".
 * estado_reserva: 1 = en el parqueadero (ocupa cupo), 2 = finalizada (ya salió), 0 = anulada.
 * hora_salida queda vacía hasta que se registra la salida.
 * El total se calcula con los costos de los servicios (la tabla no guarda el total).
 */
@Data
@Entity
@Table(name = "reserva")
public class Reserva {

    public static final short ANULADA = 0;
    public static final short ACTIVA = 1;
    public static final short FINALIZADA = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "no_documento_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "placa_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    // Usuario (administrador o controlador) que hizo la reserva
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_usuario_registra", nullable = false)
    private Usuario usuarioRegistra;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada", nullable = false)
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_servicio_parqueadero", nullable = false)
    private ServicioParqueadero servicioParqueadero;

    // Opcional: solo si el vehículo pidió lavado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_servicio_lavado")
    private ServicioLavado servicioLavado;

    @Column(name = "estado_reserva", nullable = false)
    private Short estado = ACTIVA;

    // No es columna: lo llena ReservaService desde comprobante_pago para mostrarlo
    @Transient
    private String metodoPago;

    // ---------- Valores calculados (no son columnas) ----------

    // Ejemplo: VCH-20261127-00012
    public String getCodigoVoucher() {
        if (idReserva == null) return "";
        String dia = fecha == null ? "00000000" : fecha.format(DateTimeFormatter.BASIC_ISO_DATE);
        return "VCH-" + dia + "-" + String.format("%05d", idReserva);
    }

    public BigDecimal getValorParqueadero() {
        return servicioParqueadero == null || servicioParqueadero.getCosto() == null
                ? BigDecimal.ZERO : BigDecimal.valueOf(servicioParqueadero.getCosto());
    }

    public BigDecimal getValorLavado() {
        return servicioLavado == null || servicioLavado.getCosto() == null
                ? BigDecimal.ZERO : BigDecimal.valueOf(servicioLavado.getCosto());
    }

    public BigDecimal getTotal() {
        return getValorParqueadero().add(getValorLavado());
    }

    public TipoVehiculo getTipoVehiculo() {
        return vehiculo == null ? null : vehiculo.getTipoVehiculo();
    }

    public boolean isActiva() {
        return estado != null && estado == ACTIVA;
    }

    public String getEstadoTexto() {
        if (estado == null) return "—";
        if (estado == ACTIVA) return "En parqueadero";
        if (estado == FINALIZADA) return "Finalizada";
        if (estado == ANULADA) return "Anulada";
        return "Estado " + estado;
    }
}
