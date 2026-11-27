package com.example.ejercicio.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.ejercicio.dao.UsuarioDao;
import com.example.ejercicio.domain.Usuario;
import com.example.ejercicio.service.IDashboardService;

/**
 * Agrega a TODAS las pantallas los datos que necesita el encabezado:
 * el dashboard (cupos e ingresos), el usuario actual y sus permisos.
 */
@ControllerAdvice
public class ModeloGlobal {

    @Autowired
    private IDashboardService dashboardService;

    @Autowired
    private UsuarioDao usuarioDao;

    @ModelAttribute
    public void datosComunes(Model model, Authentication authentication) {

        // En /login no hay sesión: no hay nada que calcular
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        boolean esAdmin = false;
        boolean esControlador = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMINISTRADOR")) {
                esAdmin = true;
            }
            if (authority.getAuthority().equals("ROLE_CONTROLADOR")) {
                esControlador = true;
            }
        }

        // Se muestra el nombre de la persona en vez del correo
        String nombre = usuarioDao.findFirstByCorreoIgnoreCase(authentication.getName())
                .map(Usuario::getNombreCompleto)
                .filter(n -> !n.isBlank())
                .orElse(authentication.getName());

        model.addAttribute("dash", dashboardService.obtener());
        model.addAttribute("usuarioActual", nombre + (esAdmin ? " (Administrador)" : " (Controlador)"));
        model.addAttribute("esAdmin", esAdmin);
        // ADMINISTRADOR y CONTROLADOR pueden operar todo el módulo
        model.addAttribute("puedeOperar", esAdmin || esControlador);
    }
}
