package com.example.ejercicio.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Datos que llegan del formulario "Nueva reserva".
 * No es una entidad: ReservaService la convierte en Reserva + Vehiculo + ComprobantePago validando todo.
 */
@Data
public class ReservaForm {

    private String placa;
    private Integer idTipoVehiculo;
    private Integer noDocumentoCliente;
    private Integer idServicioParqueadero;

    // Solo si se elige lavado
    private Integer idServicioLavado;

    // Pago (id de la tabla metodo_pago)
    private Integer idMetodoPago;

    // Solo informativos para el voucher (la tabla comprobante_pago no los guarda)
    private BigDecimal montoRecibido;
    private String referenciaPago;
}
