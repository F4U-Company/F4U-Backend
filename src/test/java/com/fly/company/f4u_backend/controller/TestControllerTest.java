package com.fly.company.f4u_backend.controller;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

    @InjectMocks
    private TestController testController;

    @Test
    void testHealthCheck() {
        ResponseEntity<Map<String, Object>> response = testController.healthCheck();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("OK", response.getBody().get("status"));
        assertEquals("Backend está funcionando correctamente", response.getBody().get("message"));
        assertEquals("F4U-Backend", response.getBody().get("service"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void testDatabaseStatus() {
        ResponseEntity<Map<String, Object>> response = testController.databaseStatus();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONNECTED", response.getBody().get("database"));
        assertEquals("Base de datos conectada correctamente", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void testAuthConfig() {
        ResponseEntity<Map<String, Object>> response = testController.authConfig();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("OAuth2 + JWT", response.getBody().get("authentication"));
        assertEquals("Microsoft Entra ID", response.getBody().get("provider"));
        assertEquals("USUARIO", response.getBody().get("defaultRole"));
        assertEquals("Sistema de autenticación configurado", response.getBody().get("message"));
        assertNotNull(response.getBody().get("endpoints"));
    }
}
