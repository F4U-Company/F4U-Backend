package com.fly.company.f4u_backend.controller;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class AuthControllerExtraTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @Test
    void testGetCurrentUserNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        
        ResponseEntity<Map<String, Object>> response = authController.getCurrentUser(authentication);
        
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testGetCurrentUserNullAuthentication() {
        ResponseEntity<Map<String, Object>> response = authController.getCurrentUser(null);
        
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testGetCurrentUserSuccess() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("oid")).thenReturn("user123");
        when(jwt.getClaimAsString("email")).thenReturn("test@example.com");
        when(jwt.getClaimAsString("name")).thenReturn("Test User");
        when(jwt.getClaimAsString("given_name")).thenReturn("Test");
        when(jwt.getClaimAsString("family_name")).thenReturn("User");
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        
        ResponseEntity<Map<String, Object>> response = authController.getCurrentUser(authentication);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().get("email"));
    }

    @Test
    void testGetCurrentUserException() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenThrow(new RuntimeException("Error"));
        
        ResponseEntity<Map<String, Object>> response = authController.getCurrentUser(authentication);
        
        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testGetCurrentUserRolesNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        
        ResponseEntity<List<String>> response = authController.getCurrentUserRoles(authentication);
        
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testGetCurrentUserRolesNullAuth() {
        ResponseEntity<List<String>> response = authController.getCurrentUserRoles(null);
        
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testGetCurrentUserRolesSuccess() {
        when(authentication.isAuthenticated()).thenReturn(true);
        
        ResponseEntity<List<String>> response = authController.getCurrentUserRoles(authentication);
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("USUARIO"));
    }

    @Test
    void testHasRoleNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        
        ResponseEntity<Map<String, Boolean>> response = authController.hasRole("USUARIO", authentication);
        
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testHasRoleNullAuth() {
        ResponseEntity<Map<String, Boolean>> response = authController.hasRole("USUARIO", null);
        
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testHasRoleUsuario() {
        when(authentication.isAuthenticated()).thenReturn(true);
        
        ResponseEntity<Map<String, Boolean>> response = authController.hasRole("USUARIO", authentication);
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().get("hasRole"));
    }

    @Test
    void testHasRoleAdmin() {
        when(authentication.isAuthenticated()).thenReturn(true);
        
        ResponseEntity<Map<String, Boolean>> response = authController.hasRole("ADMIN", authentication);
        
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().get("hasRole"));
    }

    @Test
    void testHasRoleCaseInsensitive() {
        when(authentication.isAuthenticated()).thenReturn(true);
        
        ResponseEntity<Map<String, Boolean>> response = authController.hasRole("usuario", authentication);
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().get("hasRole"));
    }

    @Test
    void testValidateTokenNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        
        ResponseEntity<Map<String, Object>> response = authController.validateToken(authentication);
        
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testValidateTokenNullAuth() {
        ResponseEntity<Map<String, Object>> response = authController.validateToken(null);
        
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testValidateTokenSuccess() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));
        when(jwt.getSubject()).thenReturn("user@test.com");
        
        ResponseEntity<Map<String, Object>> response = authController.validateToken(authentication);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(true, response.getBody().get("valid"));
    }

    @Test
    void testGetCurrentUserWithPreferredUsername() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("oid")).thenReturn("user456");
        when(jwt.getClaimAsString("email")).thenReturn(null);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("preferred@test.com");
        when(jwt.getClaimAsString("name")).thenReturn("Preferred User");
        when(jwt.getClaimAsString("given_name")).thenReturn("Preferred");
        when(jwt.getClaimAsString("family_name")).thenReturn("User");
        Collection<GrantedAuthority> authorities = List.of();
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        
        ResponseEntity<Map<String, Object>> response = authController.getCurrentUser(authentication);
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("preferred@test.com", response.getBody().get("email"));
    }
}
