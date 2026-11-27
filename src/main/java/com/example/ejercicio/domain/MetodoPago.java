package com.example.ejercicio.domain;

import com.example.ejercicio.util.Texto;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ENTIDAD MetodoPago: tabla "metodo_pago" (Efectivo, Tarjeta, Nequi...).
 * Los botones de pago de "Nueva reserva" salen de esta tabla.
 */
@Data
@Entity
@Table(name = "metodo_pago")
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    @Column(name = "nombre_metodo_pago", nullable = false, length = 20)
    private String nombre;

    @Column(name = "estado_metodo_pago")
    private Boolean estado = true;

    public boolean isActivo() {
        return !Boolean.FALSE.equals(estado);
    }

    // En efectivo se pide el monto recibido para calcular el cambio
    public boolean isEfectivo() {
        return Texto.sinTildes(nombre).toLowerCase().contains("efectivo");
    }

    // Ícono de Bootstrap Icons según el nombre
    public String getIcono() {
        String n = Texto.sinTildes(nombre).toLowerCase();
        if (n.contains("efectivo")) return "bi-cash-stack";
        if (n.contains("tarjeta") || n.contains("debito") || n.contains("credito")) return "bi-credit-card";
        if (n.contains("nequi") || n.contains("daviplata")) return "bi-phone";
        if (n.contains("transfer")) return "bi-bank";
        return "bi-wallet2";
    }
}
