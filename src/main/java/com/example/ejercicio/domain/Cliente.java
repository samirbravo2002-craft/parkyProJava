package com.example.ejercicio.domain;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ENTIDAD Cliente: tabla "cliente".
 * La llave es el número de documento (NO es autoincremental: se escribe en el formulario).
 * Los datos de la persona (nombre, correo, celular) están en la tabla usuario (id_usuario).
 */
@Data
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @Column(name = "no_documento_cliente")
    private Integer noDocumento;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    public String getNombreCompleto() {
        return usuario == null ? "" : usuario.getNombreCompleto();
    }
}
