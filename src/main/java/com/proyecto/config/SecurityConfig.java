package com.proyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.proyecto.service.UsuarioDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioDetailsServiceImpl usuarioDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable()) // Deshabilitado a menos que uses frontend separado

            .authorizeHttpRequests(auth -> auth
                // ---------- RUTAS PÚBLICAS ----------
                .requestMatchers(
                    "/api/usuarios/login-form",
                    "/api/usuarios/registro-form",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()

                // ---------- TODAS LAS DEMÁS RUTAS REQUIEREN AUTENTICACIÓN ----------
                .anyRequest().authenticated()
            )

            // ---------- LOGIN ----------
            .formLogin(form -> form
                .loginPage("/api/usuarios/login-form")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/api/usuarios/home", true)
                .failureUrl("/api/usuarios/login-form?error")
                .permitAll()
            )

            // ---------- LOGOUT ----------
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/api/usuarios/login-form?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
            .userDetailsService(usuarioDetailsService)
            .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
    
    
   /////to rest aapi//////
   
//    private final UsuarioDetailsServiceImpl usuarioDetailsService;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())   // deshabilita CSRF
//            .cors(cors -> cors.disable())   // deshabilita CORS
//            .authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll()   // permite todas las rutas sin autenticación
//            )
//            .formLogin(form -> form.disable()); // deshabilita login por formulario
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//
//        authBuilder
//            .userDetailsService(usuarioDetailsService)
//            .passwordEncoder(passwordEncoder());
//
//        return authBuilder.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    
//    
    
    
     
    
    
}