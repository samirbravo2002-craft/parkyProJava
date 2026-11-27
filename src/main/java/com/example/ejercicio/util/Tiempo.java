package com.example.ejercicio.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Hora oficial del parqueadero (Colombia). Se usa en todo el sistema para que
 * las reservas, el dashboard y los ingresos del día hablen del mismo "ahora".
 */
public final class Tiempo {

    public static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private Tiempo() {
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA);
    }
}
