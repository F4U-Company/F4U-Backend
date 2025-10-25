package com.fly.company.f4u_backend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomJwtAuthenticationConverter jwtAuthenticationConverter;
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    @Value("${cors.allowed.origins:http://localhost:5173,http://localhost:5174}")
    private String allowedOrigins;

    public SecurityConfig(CustomJwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    // CRÍTICO: Ignorar completamente Spring Security SOLO para endpoints de salud/debug
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // En producción, NO ignorar /api/debug/**
        if ("prod".equals(activeProfile)) {
            return (web) -> web.ignoring()
                .requestMatchers("/api/test/**", "/api/health/**", "/actuator/health", "/api/cities/**", "/api/flights/**", "/api/seats/**", "/api/seat-locks/**");
        }
        return (web) -> web.ignoring()
            .requestMatchers("/api/test/**", "/api/health/**", "/actuator/**", "/api/debug/**", "/api/cities/**", "/api/flights/**", "/api/seats/**", "/api/seat-locks/**");
    }

    // SecurityFilterChain para endpoints públicos (sin JWT) - YA NO SE USA, se ignora arriba
    @Bean
    @Order(1)
    public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/error")  // Solo manejar errores
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        return http.build();
    }

    // SecurityFilterChain para endpoints protegidos (con JWT)
    @Bean
    @Order(2)
    public SecurityFilterChain protectedFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Endpoints que requieren autenticación
                .requestMatchers("/api/auth/**").authenticated()
                .requestMatchers("/api/flights/**").authenticated()  // Proteger vuelos
                .requestMatchers("/api/reservations/**").authenticated()  // Proteger reservas
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/operator/**").hasAnyRole("OPERADOR", "ADMIN")
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter)
                )
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Obtener orígenes permitidos desde la variable de entorno
        String[] origins = allowedOrigins.split(",");
        configuration.setAllowedOrigins(Arrays.asList(origins));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight por 1 hora
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
