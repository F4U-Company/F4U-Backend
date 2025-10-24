package com.fly.company.f4u_backend.repository;

import com.fly.company.f4u_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    @Query("SELECT r FROM Role r WHERE r.isDefault = true")
    Optional<Role> findDefaultRole();
    
    boolean existsByName(String name);
}