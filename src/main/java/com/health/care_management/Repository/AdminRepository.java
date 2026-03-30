package com.health.care_management.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.health.care_management.Entity.Admin;

public interface AdminRepository extends JpaRepository<Admin,Long> {

    Optional<Admin> findByUsername(String username);
    
    
}
