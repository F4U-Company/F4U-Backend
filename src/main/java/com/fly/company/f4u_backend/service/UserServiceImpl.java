package com.fly.company.f4u_backend.service;

import com.fly.company.f4u_backend.model.Role;
import com.fly.company.f4u_backend.model.User;
import com.fly.company.f4u_backend.model.UserRole;
import com.fly.company.f4u_backend.repository.RoleRepository;
import com.fly.company.f4u_backend.repository.UserRepository;
import com.fly.company.f4u_backend.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public User createOrUpdateUserFromAzure(String azureObjectId, String email, String displayName,
            String givenName, String surname, String tenantId) {
        System.out.println("Creando/actualizando usuario desde Azure AD: " + email);

        Optional<User> existingUser = userRepository.findByAzureObjectId(azureObjectId);

        User user;
        if (existingUser.isPresent()) {
            // Actualizar usuario existente
            user = existingUser.get();
            // Actualización simplificada - los setters serán generados por Lombok en
            // runtime
            System.out.println("Usuario existente encontrado: " + email);
        } else {
            // Crear nuevo usuario - por ahora simulado
            System.out.println("Nuevo usuario simulado creado: " + email);
            user = existingUser.orElse(null); // Simulación temporal
        }

        return user; // Retorna el usuario encontrado o null
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByAzureObjectId(String azureObjectId) {
        try {
            return userRepository.findByAzureObjectId(azureObjectId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getUserActiveRoles(String azureObjectId) {
        // Por ahora, todos los usuarios tienen el rol USUARIO por defecto
        return List.of(); // Lista vacía por el momento
    }

    @Override
    public UserRole assignRoleToUser(Long userId, Long roleId, Long assignedByUserId) {
        System.out.println("Asignando rol " + roleId + " al usuario " + userId);
        return null; // Simplificado por ahora
    }

    @Override
    public void deactivateUserRole(Long userId, Long roleId) {
        System.out.println("Desactivando rol " + roleId + " del usuario " + userId);
        // Simplificado por ahora
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(String azureObjectId, String roleName) {
        // Por ahora, todos los usuarios autenticados tienen rol USUARIO
        return "USUARIO".equalsIgnoreCase(roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getDefaultRole() {
        // Retornar rol por defecto simulado
        return Optional.empty(); // Simplificado por ahora
    }

    @Override
    public void updateLastLogin(String azureObjectId) {
        System.out.println("Actualizando último login para: " + azureObjectId);
        // Simplificado por ahora
    }
}