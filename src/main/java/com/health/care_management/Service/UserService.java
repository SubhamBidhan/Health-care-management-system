package com.health.care_management.Service;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.health.care_management.Dto.UserDto;
import com.health.care_management.Entity.Role;
import com.health.care_management.Entity.User;
import com.health.care_management.Repository.RoleRepository;
import com.health.care_management.Repository.UserRepository;



@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private PasswordEncoder passwordEncoder; // For encoding passwords

    
    public User registerNewUser(UserDto userDto) {
        // Check if the user already exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        // Hash the password
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        // Create the new user
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(encodedPassword);
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setContactNumber(userDto.getContactNumber());

        // Assign default role (e.g., ROLE_USER)
        Role role = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Role not found"));

        // Initialize roles and assign the default role
        Set<Role> roles = new HashSet<>();
        roles.add(role); // Add the default ROLE_USER
        user.setRoles(roles); // Set the user's roles

        // Save the user
        return userRepository.save(user);
    }

    public Optional<User> findById(Long patientId) {
        return userRepository.findById(patientId);
    }
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }
    public void save(User user){
        userRepository.save(user);
    }

    public void deleteById(Long userId){
        userRepository.deleteById(userId);
    }
    public Optional<User> findUser(String username){
        return userRepository.findByUsername(username);
    }
}

