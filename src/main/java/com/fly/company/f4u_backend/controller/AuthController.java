package com.fly.company.f4u_backend.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }

        try {
            // Extraer información del JWT
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String azureObjectId = jwt.getClaimAsString("oid");
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");
            String givenName = jwt.getClaimAsString("given_name");
            String familyName = jwt.getClaimAsString("family_name");
            String preferredUsername = jwt.getClaimAsString("preferred_username");
            
            // Construir respuesta con datos del JWT
            Map<String, Object> response = new HashMap<>();
            response.put("azureObjectId", azureObjectId);
            response.put("email", email != null ? email : preferredUsername);
            response.put("displayName", name);
            response.put("givenName", givenName);
            response.put("surname", familyName);
            response.put("isActive", true);
            response.put("lastLogin", Instant.now());
            response.put("roles", List.of("USUARIO")); // Rol por defecto
            response.put("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getCurrentUserRoles(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        try {
            // Por ahora, todos los usuarios tienen el rol USUARIO
            return ResponseEntity.ok(List.of("USUARIO"));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/check-role/{roleName}")
    public ResponseEntity<Map<String, Boolean>> hasRole(@PathVariable String roleName, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        try {
            // Por ahora, solo verificamos si el usuario tiene el rol USUARIO
            boolean hasRole = "USUARIO".equalsIgnoreCase(roleName);
            
            return ResponseEntity.ok(Map.of("hasRole", hasRole));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("valid", false, "error", "Token inválido"));
        }

        try {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("sub", jwt.getSubject());
            response.put("iat", jwt.getIssuedAt());
            response.put("exp", jwt.getExpiresAt());
            response.put("iss", jwt.getIssuer());
            response.put("aud", jwt.getAudience());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("valid", false, "error", "Error validando token"));
        }
    }
}