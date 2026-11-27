package com.example.ejercicio.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

/**
 * ENTIDAD Usuario: tabla "usuario".
 * Aquí están los datos personales (nombre, correo, celular) y la contraseña.
 * Se inicia sesión con el CORREO (correo_usuario) y la contraseña (contrasenia).
 * Un cliente también es un usuario (con rol CLIENTE), pero el cliente NO entra a este módulo.
 */
@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;

    @Column(name = "nombre_usuario", length = 20)
    private String nombre;

    @Column(name = "apellido_usuario", length = 20)
    private String apellido;

    @Column(name = "numero_celular", length = 20)
    private String celular;

    @Column(name = "correo_usuario", length = 100, unique = true)
    private String correo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    // Puede estar en texto plano (insertado a mano) o en BCrypt. Ver ClaveFlexibleEncoder.
    @ToString.Exclude
    @Column(name = "contrasenia", length = 255)
    private String contrasenia;

    @Column(name = "estado_usuario")
    private Boolean estado = true;

    public boolean isActivo() {
        return !Boolean.FALSE.equals(estado);
    }

    public String getNombreCompleto() {
        String n = nombre == null ? "" : nombre.trim();
        String a = apellido == null ? "" : apellido.trim();
        return (n + " " + a).trim();
    }
}
