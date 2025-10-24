package com.fly.company.f4u_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final Logger log = LoggerFactory.getLogger(CustomJwtAuthenticationConverter.class);

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        // Extraer información del usuario desde el JWT de Azure AD
        String azureObjectId = jwt.getClaimAsString("oid"); // Object ID en Azure AD
        String email = jwt.getClaimAsString("preferred_username");
        if (email == null) {
            email = jwt.getClaimAsString("email");
        }
        String displayName = jwt.getClaimAsString("name");

        log.info("Usuario autenticado desde Azure AD: {} ({})", displayName, email);

        // Por ahora, asignar rol por defecto hasta que esté lista la integración completa
        Collection<GrantedAuthority> authorities = getDefaultAuthorities();

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> getDefaultAuthorities() {
        // Asignar rol USUARIO por defecto por ahora
        return List.of(new SimpleGrantedAuthority("ROLE_USUARIO"));
    }
}