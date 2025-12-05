package com.fly.company.f4u_backend.controller;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Test
    void testGetCurrentUser_NullAuthentication() {
        ResponseEntity<Map<String, Object>> response = authController.getCurrentUser(null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testGetCurrentUserRoles_NullAuthentication() {
        ResponseEntity<List<String>> response = authController.getCurrentUserRoles(null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testHasRole_NullAuthentication() {
        ResponseEntity<Map<String, Boolean>> response = authController.hasRole("USUARIO", null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testValidateToken_NullAuthentication() {
        ResponseEntity<Map<String, Object>> response = authController.validateToken(null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse((Boolean) response.getBody().get("valid"));
    }
}
