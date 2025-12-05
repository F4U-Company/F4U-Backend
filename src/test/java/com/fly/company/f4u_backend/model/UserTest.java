package com.fly.company.f4u_backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testSetAndGetId() {
        user.setId(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    void testSetAndGetAzureObjectId() {
        user.setAzureObjectId("azure-123");
        assertEquals("azure-123", user.getAzureObjectId());
    }

    @Test
    void testSetAndGetEmail() {
        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testSetAndGetDisplayName() {
        user.setDisplayName("Test User");
        assertEquals("Test User", user.getDisplayName());
    }

    @Test
    void testSetAndGetGivenName() {
        user.setGivenName("Test");
        assertEquals("Test", user.getGivenName());
    }

    @Test
    void testSetAndGetSurname() {
        user.setSurname("User");
        assertEquals("User", user.getSurname());
    }

    @Test
    void testSetAndGetTenantId() {
        user.setTenantId("tenant-123");
        assertEquals("tenant-123", user.getTenantId());
    }

    @Test
    void testSetAndGetIsActive() {
        user.setIsActive(true);
        assertTrue(user.getIsActive());
    }

    @Test
    void testConstructorWithBasicFields() {
        User newUser = new User("azure-456", "new@example.com", "New User", "tenant-456");
        assertEquals("azure-456", newUser.getAzureObjectId());
        assertEquals("new@example.com", newUser.getEmail());
        assertEquals("New User", newUser.getDisplayName());
        assertEquals("tenant-456", newUser.getTenantId());
    }

    @Test
    void testConstructorWithAllFields() {
        User newUser = new User("azure-789", "full@example.com", "Full Name", "Full", "Name", "tenant-789");
        assertEquals("azure-789", newUser.getAzureObjectId());
        assertEquals("full@example.com", newUser.getEmail());
        assertEquals("Full Name", newUser.getDisplayName());
        assertEquals("Full", newUser.getGivenName());
        assertEquals("Name", newUser.getSurname());
        assertEquals("tenant-789", newUser.getTenantId());
    }

    @Test
    void testDefaultValues() {
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getDisplayName());
    }
}
