package com.example.ejercicio.web;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Permite entrar con las contraseñas que creaste directamente en la base de datos:
 *  - Si la contraseña guardada es un hash BCrypt ($2a$...), la compara con BCrypt.
 *  - Si está en texto plano (ej. insertada con un INSERT), la compara tal cual.
 * Las contraseñas nuevas siempre se guardan en BCrypt, y las de texto plano se
 * convierten a BCrypt automáticamente la primera vez que el usuario entra bien.
 */
public class ClaveFlexibleEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence clave) {
        return bcrypt.encode(clave);
    }

    @Override
    public boolean matches(CharSequence clave, String guardada) {
        if (clave == null || guardada == null) {
            return false;
        }
        if (esBcrypt(guardada)) {
            return bcrypt.matches(clave, guardada);
        }
        return MessageDigest.isEqual(
                clave.toString().getBytes(StandardCharsets.UTF_8),
                guardada.getBytes(StandardCharsets.UTF_8));
    }

    // true = la contraseña guardada todavía no está en BCrypt
    @Override
    public boolean upgradeEncoding(String guardada) {
        return guardada != null && !esBcrypt(guardada);
    }

    private static boolean esBcrypt(String s) {
        return s.matches("^\\$2[aby]?\\$\\d{2}\\$[./A-Za-z0-9]{53}$");
    }
}
