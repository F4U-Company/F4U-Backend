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

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        if ("prod".equals(activeProfile)) {
            return (web) -> web.ignoring()
                .requestMatchers("/api/test/**", "/api/health/**", "/actuator/health", "/api/cities/**", "/api/flights/**", "/api/seats/**", "/api/seat-locks/**", "/api/reservaciones/**");
        }
        return (web) -> web.ignoring()
            .requestMatchers("/api/test/**", "/api/health/**", "/actuator/**", "/api/debug/**", "/api/cities/**", "/api/flights/**", "/api/seats/**", "/api/seat-locks/**", "/api/reservaciones/**");
    }

    @Bean
    @Order(1)
    public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/error")
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain protectedFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/cities/**", "/api/flights/**", "/api/seats/**", "/api/seat-locks/**", "/api/reservaciones/**", "/api/test/**", "/api/health/**").permitAll()
                .requestMatchers("/api/auth/**", "/api/chatbot/**").authenticated()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/operator/**").hasAnyRole("OPERADOR", "ADMIN")
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
        
        // ✅ SOLUCIÓN: Usar allowedOriginPatterns en lugar de allowedOrigins
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:5174", 
            "https://delightful-tree-0a83b250f.1.azurestaticapps.net",
            "http://localhost:8080"
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}