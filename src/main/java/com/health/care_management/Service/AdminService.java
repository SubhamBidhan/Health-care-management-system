package com.health.care_management.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.health.care_management.Entity.Admin;
import com.health.care_management.Repository.AdminRepository;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;

    public Optional<Admin> findByUsername(String username){
        return adminRepository.findByUsername(username);
    }
    public void save(Admin admin){
        adminRepository.save(admin);
    }
}
