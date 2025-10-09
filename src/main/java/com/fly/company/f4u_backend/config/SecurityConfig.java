package com.fly.company.f4u_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
                .anyRequest().permitAll()   // <-- permitir todo en dev
            .and()
            .httpBasic().disable();

        // permitir consola H2 en iframe (si usas la consola)
        http.headers().frameOptions().disable();

        return http.build();
    }
}
