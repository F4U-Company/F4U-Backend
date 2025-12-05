package com.fly.company.f4u_backend.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fly.company.f4u_backend.model.Role;
import com.fly.company.f4u_backend.model.User;
import com.fly.company.f4u_backend.repository.RoleRepository;
import com.fly.company.f4u_backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setDisplayName("Test User");
    }

    @Test
    void testCreateOrUpdateUserFromAzure_UserExists() {
        when(userRepository.findByAzureObjectId("azure-123")).thenReturn(Optional.of(user));

        User result = userService.createOrUpdateUserFromAzure(
            "azure-123", "test@example.com", "Test User", "Test", "User", "tenant-123"
        );

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateOrUpdateUserFromAzure_NewUser() {
        when(userRepository.findByAzureObjectId("azure-456")).thenReturn(Optional.empty());

        User result = userService.createOrUpdateUserFromAzure(
            "azure-456", "new@example.com", "New User", "New", "User", "tenant-123"
        );

        assertNull(result);
    }

    @Test
    void testFindByEmail_Found() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testFindByEmail_NotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("notfound@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByEmail_Exception() {
        when(userRepository.findByEmail(anyString())).thenThrow(new RuntimeException("DB error"));

        Optional<User> result = userService.findByEmail("error@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByAzureObjectId_Found() {
        when(userRepository.findByAzureObjectId(anyString())).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByAzureObjectId("test-azure-id");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(userRepository, times(1)).findByAzureObjectId("test-azure-id");
    }

    @Test
    void testFindByAzureObjectId_NotFound() {
        when(userRepository.findByAzureObjectId(anyString())).thenReturn(Optional.empty());

        Optional<User> result = userService.findByAzureObjectId("not-found-id");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByAzureObjectId_Exception() {
        when(userRepository.findByAzureObjectId(anyString())).thenThrow(new RuntimeException("DB error"));

        Optional<User> result = userService.findByAzureObjectId("error-id");

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserActiveRoles() {
        List<Role> roles = userService.getUserActiveRoles("test-azure-id");

        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    void testHasRole_Usuario() {
        boolean result = userService.hasRole("azure-id-123", "USUARIO");

        assertTrue(result);
    }

    @Test
    void testHasRole_OtherRole() {
        boolean result = userService.hasRole("azure-id-123", "ADMIN");

        assertFalse(result);
    }
}
