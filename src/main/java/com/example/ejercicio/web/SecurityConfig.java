package com.example.ejercicio.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.ejercicio.service.UsuarioDetailService;

/**
 * Módulo de CONTROL: solo entran ADMINISTRADOR y CONTROLADOR (con los usuarios de tu base de datos).
 * Los dos pueden ver el dashboard y gestionar reservas, clientes, limpiadores y servicios.
 */
@Configuration
public class SecurityConfig {

    @Bean // acepta contraseñas en BCrypt o en texto plano (ver ClaveFlexibleEncoder)
    public PasswordEncoder passwordEncoder() {
        return new ClaveFlexibleEncoder();
    }

    @Bean // compara las contraseñas
    public DaoAuthenticationProvider authenticationProvider(
            UsuarioDetailService usuarioDetailService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(usuarioDetailService);
        authProvider.setPasswordEncoder(passwordEncoder);
        // Si la clave estaba en texto plano, al entrar se guarda en BCrypt
        authProvider.setUserDetailsPasswordService(usuarioDetailService);
        return authProvider;
    }

    @Bean // reglas de seguridad
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider) throws Exception {

        http.authenticationProvider(authenticationProvider);

        http.authorizeHttpRequests(auth -> auth
                // se puede ver sin iniciar sesión
                .requestMatchers("/css/**", "/js/**", "/img/**", "/login", "/error").permitAll()

                // Si quieres que SOLO el administrador cambie los servicios, descomenta esta línea:
                // .requestMatchers(org.springframework.http.HttpMethod.POST, "/servicios/**").hasRole("ADMINISTRADOR")

                // todo lo demás: administrador o controlador
                .anyRequest().hasAnyRole("ADMINISTRADOR", "CONTROLADOR"));

        http.formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll());

        // El cierre de sesión es por POST (con token CSRF): ver el botón en el encabezado
        http.logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll());

        return http.build();
    }
}
