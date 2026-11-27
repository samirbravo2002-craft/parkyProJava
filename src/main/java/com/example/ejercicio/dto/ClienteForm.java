package com.example.ejercicio.dto;

import com.example.ejercicio.domain.Cliente;
import com.example.ejercicio.domain.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Formulario de cliente. Un cliente se guarda en DOS tablas:
 *  - usuario: tipo de documento, nombre, apellido, celular, correo (rol CLIENTE)
 *  - cliente: número de documento + id_usuario
 * Por eso el formulario no usa la entidad directamente.
 */
@Data
public class ClienteForm {

    // true = crear, false = editar (al editar el documento no se puede cambiar: es la llave)
    private boolean nuevo = true;

    @NotNull(message = "Escribe el número de documento")
    @Positive(message = "El documento debe ser un número positivo")
    private Integer noDocumento;

    @NotBlank(message = "Selecciona el tipo de documento")
    @Size(max = 20)
    private String tipoDocumento;

    @NotBlank(message = "Escribe el nombre")
    @Size(max = 20, message = "Máximo 20 caracteres")
    private String nombre;

    @NotBlank(message = "Escribe el apellido")
    @Size(max = 20, message = "Máximo 20 caracteres")
    private String apellido;

    @NotBlank(message = "Escribe el correo")
    @Email(message = "Correo no válido")
    @Size(max = 100)
    private String correo;

    @NotBlank(message = "Escribe el celular")
    @Pattern(regexp = "^[0-9]{7,20}$", message = "Solo números (7 a 20 dígitos)")
    private String celular;

    public static ClienteForm desde(Cliente c) {
        ClienteForm f = new ClienteForm();
        f.setNuevo(false);
        f.setNoDocumento(c.getNoDocumento());
        Usuario u = c.getUsuario();
        if (u != null) {
            f.setTipoDocumento(u.getTipoDocumento());
            f.setNombre(u.getNombre());
            f.setApellido(u.getApellido());
            f.setCorreo(u.getCorreo());
            f.setCelular(u.getCelular());
        }
        return f;
    }
}
