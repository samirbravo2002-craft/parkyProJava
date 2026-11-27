package com.example.ejercicio.util;

import java.text.Normalizer;

/**
 * Ayudas para comparar textos que vienen de la base de datos
 * (sin importar mayúsculas, tildes o espacios).
 */
public final class Texto {

    private Texto() {
    }

    public static String sinTildes(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    /**
     * Convierte el nombre del rol de la base de datos al nombre que usa Spring Security.
     * "Administrador", "ADMIN", "administrador " -> ADMINISTRADOR
     * "Controlador" -> CONTROLADOR ; "Cliente" -> CLIENTE
     */
    public static String normalizarRol(String nombre) {
        String s = sinTildes(nombre).trim().toUpperCase().replaceFirst("^ROLE_", "");
        if (s.startsWith("ADMIN")) return "ADMINISTRADOR";
        if (s.startsWith("CONTROL")) return "CONTROLADOR";
        if (s.startsWith("CLIENT")) return "CLIENTE";
        return s.replaceAll("\\s+", "_");
    }

    // "Carro" -> carro ; "Moto Eléctrica" -> moto-electrica (para leer application.properties)
    public static String slug(String s) {
        return sinTildes(s).trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
