package com.example.ejercicio.domain;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ENTIDAD Rol: tabla "rol".
 * En la base de datos el nombre va SIN el prefijo "ROLE_" (ADMINISTRADOR, CONTROLADOR, CLIENTE).
 */
@Data
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre_rol", length = 20)
    private String nombre;

    @Column(name = "estado_rol")
    private Boolean estado = true;
}
