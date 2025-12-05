package com.fly.company.f4u_backend.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fly.company.f4u_backend.model.Role;
import com.fly.company.f4u_backend.model.User;
import com.fly.company.f4u_backend.repository.RoleRepository;
import com.fly.company.f4u_backend.repository.UserRepository;
import com.fly.company.f4u_backend.repository.UserRoleRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceExtraTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testFindByAzureObjectIdExists() {
        User user = new User();
        user.setAzureObjectId("azure123");
        user.setEmail("test@example.com");

        when(userRepository.findByAzureObjectId("azure123"))
            .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByAzureObjectId("azure123");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findByAzureObjectId("azure123");
    }

    @Test
    void testFindByAzureObjectIdNotFound() {
        when(userRepository.findByAzureObjectId("nonexistent"))
            .thenReturn(Optional.empty());

        Optional<User> result = userService.findByAzureObjectId("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByAzureObjectIdException() {
        when(userRepository.findByAzureObjectId(anyString()))
            .thenThrow(new RuntimeException("Database error"));

        Optional<User> result = userService.findByAzureObjectId("error");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByEmailExists() {
        User user = new User();
        user.setEmail("found@test.com");

        when(userRepository.findByEmail("found@test.com"))
            .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("found@test.com");

        assertTrue(result.isPresent());
        assertEquals("found@test.com", result.get().getEmail());
    }

    @Test
    void testFindByEmailNotFound() {
        when(userRepository.findByEmail("notfound@test.com"))
            .thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("notfound@test.com");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByEmailException() {
        when(userRepository.findByEmail(anyString()))
            .thenThrow(new RuntimeException("Connection failed"));

        Optional<User> result = userService.findByEmail("error@test.com");

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserActiveRoles() {
        List<Role> roles = userService.getUserActiveRoles("azure123");

        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    void testCreateOrUpdateUserFromAzureExisting() {
        User existingUser = new User();
        existingUser.setAzureObjectId("azure456");
        existingUser.setEmail("existing@test.com");

        when(userRepository.findByAzureObjectId("azure456"))
            .thenReturn(Optional.of(existingUser));

        User result = userService.createOrUpdateUserFromAzure(
            "azure456", "existing@test.com", "John Doe", "John", "Doe", "tenant123"
        );

        assertNotNull(result);
        assertEquals("existing@test.com", result.getEmail());
    }

    @Test
    void testCreateOrUpdateUserFromAzureNew() {
        when(userRepository.findByAzureObjectId("azure789"))
            .thenReturn(Optional.empty());

        User result = userService.createOrUpdateUserFromAzure(
            "azure789", "new@test.com", "Jane Smith", "Jane", "Smith", "tenant456"
        );

        // Simulación temporal retorna null para nuevos usuarios
        assertNull(result);
    }

    @Test
    void testHasRole() {
        assertFalse(userService.hasRole("azure123", "ADMIN"));
    }

    @Test
    void testFindByEmailMultipleAttempts() {
        User u1 = new User();
        u1.setEmail("user1@test.com");
        User u2 = new User();
        u2.setEmail("user2@test.com");

        when(userRepository.findByEmail("user1@test.com")).thenReturn(Optional.of(u1));
        when(userRepository.findByEmail("user2@test.com")).thenReturn(Optional.of(u2));
        when(userRepository.findByEmail("user3@test.com")).thenReturn(Optional.empty());

        Optional<User> r1 = userService.findByEmail("user1@test.com");
        Optional<User> r2 = userService.findByEmail("user2@test.com");
        Optional<User> r3 = userService.findByEmail("user3@test.com");

        assertTrue(r1.isPresent());
        assertTrue(r2.isPresent());
        assertFalse(r3.isPresent());
        verify(userRepository, times(3)).findByEmail(anyString());
    }

    @Test
    void testFindByAzureObjectIdWithSpecialCharacters() {
        User user = new User();
        user.setAzureObjectId("azure-123-456-xyz");
        user.setEmail("special@test.com");

        when(userRepository.findByAzureObjectId("azure-123-456-xyz"))
            .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByAzureObjectId("azure-123-456-xyz");

        assertTrue(result.isPresent());
        assertEquals("azure-123-456-xyz", result.get().getAzureObjectId());
    }
}
