package com.fly.company.f4u_backend.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    @Test
    void testUserSessionDefaultConstructor() {
        UserSession session = new UserSession();
        assertNotNull(session);
    }

    @Test
    void testUserSessionParameterizedConstructor() {
        User user = new User();
        String token = "test-token";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        
        UserSession session = new UserSession(user, token, expiresAt);
        
        assertEquals(user, session.getUser());
        assertEquals(token, session.getSessionToken());
    }

    @Test
    void testGetSetId() {
        UserSession session = new UserSession();
        session.setId(100L);
        assertEquals(100L, session.getId());
    }

    @Test
    void testGetSetUser() {
        UserSession session = new UserSession();
        User user = new User();
        session.setUser(user);
        assertEquals(user, session.getUser());
    }

    @Test
    void testGetSetSessionToken() {
        UserSession session = new UserSession();
        session.setSessionToken("abc123");
        assertEquals("abc123", session.getSessionToken());
    }

    @Test
    void testGetSetIsActive() {
        UserSession session = new UserSession();
        session.setIsActive(false);
        assertFalse(session.getIsActive());
    }

    @Test
    void testUserSessionWithNullUser() {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        UserSession session = new UserSession(null, "token", expiresAt);
        assertNull(session.getUser());
    }

    @Test
    void testUserSessionWithNullToken() {
        User user = new User();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        UserSession session = new UserSession(user, null, expiresAt);
        assertNull(session.getSessionToken());
    }

    @Test
    void testUserSessionActiveByDefault() {
        UserSession session = new UserSession();
        assertTrue(session.getIsActive());
    }

    @Test
    void testUserSessionIdNullInitially() {
        UserSession session = new UserSession();
        assertNull(session.getId());
    }
}
