package com.fly.company.f4u_backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    void testRoleCreation() {
        Role role = new Role();
        role.setName("ADMIN");
        role.setDescription("Administrator role");
        
        assertNotNull(role);
        assertEquals("ADMIN", role.getName());
        assertEquals("Administrator role", role.getDescription());
    }

    @Test
    void testRoleDefault() {
        Role role = new Role();
        
        assertFalse(role.getIsDefault());
        
        role.setIsDefault(true);
        assertTrue(role.getIsDefault());
    }

    @Test
    void testRoleAllFields() {
        Role role = new Role();
        role.setName("USER");
        role.setDescription("Standard user role");
        role.setIsDefault(true);
        
        assertEquals("USER", role.getName());
        assertEquals("Standard user role", role.getDescription());
        assertTrue(role.getIsDefault());
        assertNotNull(role.getUserRoles());
    }

    @Test
    void testRoleNameUniqueness() {
        Role role1 = new Role();
        role1.setName("ADMIN");
        
        Role role2 = new Role();
        role2.setName("ADMIN");
        
        assertEquals(role1.getName(), role2.getName());
    }
}
