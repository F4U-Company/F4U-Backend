package com.fly.company.f4u_backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/token-info")
    public ResponseEntity<?> getTokenInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        // Información del header
        response.put("authHeaderPresent", authHeader != null);
        if (authHeader != null) {
            response.put("authHeaderValue", authHeader.substring(0, Math.min(50, authHeader.length())) + "...");
        }
        
        // Información de la autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        response.put("authenticated", authentication != null && authentication.isAuthenticated());
        
        if (authentication != null) {
            response.put("principal", authentication.getPrincipal().getClass().getSimpleName());
            response.put("authorities", authentication.getAuthorities().toString());
            
            // Si es un JWT, mostrar claims
            if (authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                Map<String, Object> claims = new HashMap<>();
                claims.put("subject", jwt.getSubject());
                claims.put("issuer", jwt.getIssuer());
                claims.put("audience", jwt.getAudience());
                claims.put("expiresAt", jwt.getExpiresAt());
                claims.put("issuedAt", jwt.getIssuedAt());
                claims.put("allClaims", jwt.getClaims());
                response.put("jwtClaims", claims);
            }
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/public")
    public ResponseEntity<?> publicEndpoint() {
        return ResponseEntity.ok(Map.of(
            "message", "Este endpoint es público y no requiere autenticación",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
