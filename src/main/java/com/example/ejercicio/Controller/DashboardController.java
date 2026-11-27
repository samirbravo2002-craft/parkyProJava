package com.example.ejercicio.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Devuelve SOLO el bloque del dashboard. El navegador lo pide cada 15 segundos
 * para que los cupos y el dinero se actualicen sin recargar la página.
 */
@Controller
public class DashboardController {

    @GetMapping("/dashboard/fragmento")
    public String fragmento() {
        return "fragments/layout :: dashboard";
    }
}
