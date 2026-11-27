package com.example.ejercicio.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ENTIDAD Limpiador: tabla "limpiador".
 * Los nombres de @Column son IGUALES a los del script SQL.
 */
@Data
@Entity
@Table(name = "limpiador")
public class Limpiador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_limpiador")
    private Integer idLimpiador;

    @NotBlank(message = "Selecciona el tipo de documento")
    @Size(max = 20)
    @Column(name = "tipo_documento_limpiador", length = 20)
    private String tipoDocumento;

    @NotBlank(message = "Escribe el número de documento")
    @Pattern(regexp = "^[A-Za-z0-9]{3,20}$", message = "Solo letras y números (3 a 20)")
    @Column(name = "numero_documento_limpiador", length = 20)
    private String numeroDocumento;

    @NotBlank(message = "Escribe el nombre")
    @Size(max = 20, message = "Máximo 20 caracteres")
    @Column(name = "nombre_limpiador", length = 20)
    private String nombre;

    @NotBlank(message = "Escribe el apellido")
    @Size(max = 20, message = "Máximo 20 caracteres")
    @Column(name = "apellido_limpiador", length = 20)
    private String apellido;

    @NotBlank(message = "Escribe el celular")
    @Pattern(regexp = "^[0-9]{7,20}$", message = "Solo números (7 a 20 dígitos)")
    @Column(name = "numero_celular_limpiador", length = 20)
    private String celular;

    @NotBlank(message = "Escribe el correo")
    @Email(message = "Correo no válido")
    @Size(max = 100)
    @Column(name = "correo_limpiador", length = 100, unique = true)
    private String correo;

    @Column(name = "estado_limpiador")
    private Boolean estado = true;

    public boolean isActivo() {
        return !Boolean.FALSE.equals(estado);
    }
}
