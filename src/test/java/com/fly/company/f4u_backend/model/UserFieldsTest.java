package com.fly.company.f4u_backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UserFieldsTest {

    @Test
    void testUserDefaultConstructor() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void testUserFourParamConstructor() {
        User user = new User("azure123", "test@example.com", "Test User", "tenant123");
        assertEquals("azure123", user.getAzureObjectId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getDisplayName());
        assertEquals("tenant123", user.getTenantId());
    }

    @Test
    void testUserSixParamConstructor() {
        User user = new User("azure123", "test@example.com", "Test User", "John", "Doe", "tenant123");
        assertEquals("azure123", user.getAzureObjectId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getDisplayName());
        assertEquals("John", user.getGivenName());
        assertEquals("Doe", user.getSurname());
        assertEquals("tenant123", user.getTenantId());
    }

    @Test
    void testGetSetAzureObjectId() {
        User user = new User();
        user.setAzureObjectId("azure456");
        assertEquals("azure456", user.getAzureObjectId());
    }

    @Test
    void testGetSetEmail() {
        User user = new User();
        user.setEmail("user@test.com");
        assertEquals("user@test.com", user.getEmail());
    }

    @Test
    void testGetSetDisplayName() {
        User user = new User();
        user.setDisplayName("Display Name");
        assertEquals("Display Name", user.getDisplayName());
    }

    @Test
    void testGetSetGivenName() {
        User user = new User();
        user.setGivenName("Jane");
        assertEquals("Jane", user.getGivenName());
    }

    @Test
    void testGetSetSurname() {
        User user = new User();
        user.setSurname("Smith");
        assertEquals("Smith", user.getSurname());
    }

    @Test
    void testGetSetTenantId() {
        User user = new User();
        user.setTenantId("tenant789");
        assertEquals("tenant789", user.getTenantId());
    }

    @Test
    void testGetSetIsActive() {
        User user = new User();
        user.setIsActive(true);
        assertTrue(user.getIsActive());
        user.setIsActive(false);
        assertFalse(user.getIsActive());
    }

    @Test
    void testGetSetLastLogin() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();
        user.setLastLogin(now);
        assertEquals(now, user.getLastLogin());
    }

    @Test
    void testGetSetCreatedAt() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void testGetSetUpdatedAt() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();
        user.setUpdatedAt(now);
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testGetSetUserRoles() {
        User user = new User();
        Set<UserRole> roles = new HashSet<>();
        UserRole role = new UserRole();
        roles.add(role);
        user.setUserRoles(roles);
        assertEquals(roles, user.getUserRoles());
        assertEquals(1, user.getUserRoles().size());
    }

    @Test
    void testGetSetUserSessions() {
        User user = new User();
        Set<UserSession> sessions = new HashSet<>();
        UserSession session = new UserSession();
        sessions.add(session);
        user.setUserSessions(sessions);
        assertEquals(sessions, user.getUserSessions());
        assertEquals(1, user.getUserSessions().size());
    }

    @Test
    void testUserWithAllFields() {
        User user = new User();
        user.setId(100L);
        user.setAzureObjectId("azure999");
        user.setEmail("complete@test.com");
        user.setDisplayName("Complete User");
        user.setGivenName("Complete");
        user.setSurname("Test");
        user.setTenantId("tenant999");
        user.setIsActive(true);
        
        assertEquals(100L, user.getId());
        assertEquals("azure999", user.getAzureObjectId());
        assertEquals("complete@test.com", user.getEmail());
        assertEquals("Complete User", user.getDisplayName());
        assertEquals("Complete", user.getGivenName());
        assertEquals("Test", user.getSurname());
        assertEquals("tenant999", user.getTenantId());
        assertTrue(user.getIsActive());
    }
}
