package com.fly.company.f4u_backend.repository;

import com.fly.company.f4u_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByAzureObjectId(String azureObjectId);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.azureObjectId = :azureObjectId AND u.tenantId = :tenantId")
    Optional<User> findByAzureObjectIdAndTenantId(@Param("azureObjectId") String azureObjectId, @Param("tenantId") String tenantId);
    
    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.name = :roleName AND ur.isActive = true")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);
    
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);
    
    List<User> findByIsActiveTrue();
    
    boolean existsByEmail(String email);
    
    boolean existsByAzureObjectId(String azureObjectId);
}