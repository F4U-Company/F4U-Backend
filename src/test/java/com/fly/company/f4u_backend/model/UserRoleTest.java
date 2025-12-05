package com.fly.company.f4u_backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UserRoleTest {

    @Test
    void testUserRoleCreation() {
        User user = new User();
        user.setId(1L);
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        
        UserRole userRole = new UserRole(user, role);
        
        assertNotNull(userRole);
        assertEquals(user, userRole.getUser());
        assertEquals(role, userRole.getRole());
    }

    @Test
    void testUserRoleActive() {
        UserRole userRole = new UserRole();
        
        assertTrue(userRole.getIsActive());
        
        userRole.setIsActive(false);
        assertFalse(userRole.getIsActive());
    }

    @Test
    void testUserRoleAssignedBy() {
        UserRole userRole = new UserRole();
        User assignedBy = new User();
        assignedBy.setId(2L);
        
        userRole.setAssignedBy(assignedBy);
        
        assertEquals(assignedBy, userRole.getAssignedBy());
    }

    @Test
    void testUserRoleAllFields() {
        User user = new User();
        user.setId(1L);
        Role role = new Role();
        role.setId(1L);
        User assignedBy = new User();
        assignedBy.setId(2L);
        
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedBy(assignedBy);
        userRole.setIsActive(true);
        
        assertEquals(user, userRole.getUser());
        assertEquals(role, userRole.getRole());
        assertEquals(assignedBy, userRole.getAssignedBy());
        assertTrue(userRole.getIsActive());
    }
}
