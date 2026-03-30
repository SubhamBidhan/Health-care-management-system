package com.health.care_management.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.health.care_management.Entity.Doctor;
import com.health.care_management.Entity.User;
import com.health.care_management.Entity.Admin;
import com.health.care_management.Repository.DoctorRepository;
import com.health.care_management.Repository.UserRepository;
import com.health.care_management.Repository.AdminRepository; // Admin Repository

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AdminRepository adminRepository; // Add Admin repository
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First, try to find the user in the UserRepository
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null) {
            return UserDetailsImpl.build(user); 
        }

        // If not found, try finding the doctor in DoctorRepository
        Doctor doctor = doctorRepository.findByUsername(username).orElse(null);
        if (doctor != null) {
            return UserDetailsImpl.buildFromDoctor(doctor);
        }

        // If not found, try finding the admin in AdminRepository
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User, Doctor, or Admin Not Found with username: " + username));

        return UserDetailsImpl.buildFromAdmin(admin); // Build UserDetails for Admin
    }
}
