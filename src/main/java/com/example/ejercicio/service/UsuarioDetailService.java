package com.example.ejercicio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ejercicio.dao.UsuarioDao;
import com.example.ejercicio.domain.Usuario;
import com.example.ejercicio.util.Texto;

/**
 * Spring Security le pregunta a esta clase: "¿existe este usuario? dame su contraseña y su rol".
 *
 * - Se inicia sesión con el CORREO (correo_usuario) y la contraseña (contrasenia) de TU tabla usuario.
 * - Solo pueden entrar los roles ADMINISTRADOR y CONTROLADOR (el cliente no entra a este módulo).
 * - Si la contraseña estaba en texto plano, al primer ingreso correcto se reemplaza por su
 *   versión BCrypt (updatePassword). La clave que escribes sigue siendo la misma.
 */
@Service
public class UsuarioDetailService implements UserDetailsService, UserDetailsPasswordService {

    @Autowired
    private UsuarioDao usuarioDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        Usuario usuario = usuarioDao.findFirstByCorreoIgnoreCase(correo == null ? "" : correo.trim())
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario: " + correo));

        String rol = Texto.normalizarRol(usuario.getRol() == null ? null : usuario.getRol().getNombre());
        if (!rol.equals("ADMINISTRADOR") && !rol.equals("CONTROLADOR")) {
            throw new UsernameNotFoundException("El usuario no tiene acceso al módulo de control: " + correo);
        }
        if (usuario.getContrasenia() == null || usuario.getContrasenia().isBlank()) {
            throw new UsernameNotFoundException("El usuario no tiene contraseña: " + correo);
        }

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasenia())
                .authorities("ROLE_" + rol)                 // ROLE_ADMINISTRADOR o ROLE_CONTROLADOR
                .disabled(!usuario.isActivo())              // estado_usuario = false -> no puede entrar
                .build();
    }

    // Guarda la contraseña en BCrypt cuando estaba en texto plano
    @Override
    @Transactional
    public UserDetails updatePassword(UserDetails user, String nuevaClaveCodificada) {
        usuarioDao.findFirstByCorreoIgnoreCase(user.getUsername()).ifPresent(u -> {
            u.setContrasenia(nuevaClaveCodificada);
            usuarioDao.save(u);
        });
        return User.withUserDetails(user).password(nuevaClaveCodificada).build();
    }
}
