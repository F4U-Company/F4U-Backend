package com.fly.company.f4u_backend.service;

import java.util.List;
import java.util.Optional;

import com.fly.company.f4u_backend.model.Role;
import com.fly.company.f4u_backend.model.User;
import com.fly.company.f4u_backend.model.UserRole;

public interface UserService {
    
    /**
     * Crear o actualizar usuario desde Microsoft Entra ID
     */
    User createOrUpdateUserFromAzure(String azureObjectId, String email, String displayName, 
                                   String givenName, String surname, String tenantId);
    
    /**
     * Buscar usuario por Azure Object ID
     */
    Optional<User> findByAzureObjectId(String azureObjectId);
    
    /**
     * Buscar usuario por email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Obtener roles activos del usuario
     */
    List<Role> getUserActiveRoles(String azureObjectId);
    
    /**
     * Asignar rol a usuario
     */
    UserRole assignRoleToUser(Long userId, Long roleId, Long assignedByUserId);
    
    /**
     * Desactivar rol del usuario
     */
    void deactivateUserRole(Long userId, Long roleId);
    
    /**
     * Verificar si el usuario tiene un rol específico
     */
    boolean hasRole(String azureObjectId, String roleName);
    
    /**
     * Obtener rol por defecto
     */
    Optional<Role> getDefaultRole();
    
    /**
     * Actualizar último login del usuario
     */
    void updateLastLogin(String azureObjectId);
}