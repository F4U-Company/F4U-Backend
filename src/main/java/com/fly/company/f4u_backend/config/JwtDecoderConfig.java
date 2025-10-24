package com.fly.company.f4u_backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Configuración personalizada del decodificador JWT para modo multi-tenant
 * Valida audience, issuer y timestamp del token
 */
@Configuration
public class JwtDecoderConfig {
    
    @Value("${spring.security.oauth2.resourceserver.jwt.audiences}")
    private String expectedAudience;
    
    @Value("${azure.activedirectory.client-id}")
    private String clientId;

    @Bean
    public JwtDecoder jwtDecoder() {
        // Para modo multi-tenant, usar JWK Set URI directamente
        // En lugar de issuer-uri que causa problemas con /common/
        String jwkSetUri = "https://login.microsoftonline.com/common/discovery/v2.0/keys";
        
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        
        // Validar timestamp
        OAuth2TokenValidator<Jwt> withTimestamp = new JwtTimestampValidator();
        
        // Validar audience - acepta tanto el formato api:// como el Client ID solo
        OAuth2TokenValidator<Jwt> withAudience = new JwtClaimValidator<List<String>>(
            JwtClaimNames.AUD,
            aud -> aud != null && (
                aud.contains(expectedAudience) || 
                aud.contains(clientId)
            )
        );
        
        // Validar que el issuer sea de Microsoft (cualquier tenant)
        OAuth2TokenValidator<Jwt> withIssuer = new JwtClaimValidator<String>(
            JwtClaimNames.ISS,
            iss -> iss != null && iss.startsWith("https://login.microsoftonline.com/")
        );
        
        jwtDecoder.setJwtValidator(
            new DelegatingOAuth2TokenValidator<>(withTimestamp, withAudience, withIssuer)
        );
        
        return jwtDecoder;
    }
}
