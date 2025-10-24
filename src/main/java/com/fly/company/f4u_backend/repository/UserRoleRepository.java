package com.fly.company.f4u_backend.repository;

import com.fly.company.f4u_backend.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId AND ur.isActive = true")
    List<UserRole> findActiveRolesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.id = :roleId AND ur.isActive = true")
    Optional<UserRole> findActiveUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    @Query("SELECT ur FROM UserRole ur JOIN FETCH ur.role WHERE ur.user.azureObjectId = :azureObjectId AND ur.isActive = true")
    List<UserRole> findActiveRolesByAzureObjectId(@Param("azureObjectId") String azureObjectId);
    
    @Query("SELECT ur FROM UserRole ur JOIN ur.role r WHERE r.name = :roleName AND ur.isActive = true")
    List<UserRole> findByRoleName(@Param("roleName") String roleName);
    
    boolean existsByUserIdAndRoleIdAndIsActiveTrue(Long userId, Long roleId);
}