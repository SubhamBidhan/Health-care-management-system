package com.health.care_management.Service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.health.care_management.Entity.User;
import com.health.care_management.Entity.Admin;
import com.health.care_management.Entity.Doctor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String username;
    private String email;
    private String password; // Add this line to hold the user's password
    private Collection<? extends GrantedAuthority> authorities;

    // Constructor for User
    public UserDetailsImpl(Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password; // Initialize password
        this.authorities = authorities;
    }

    // Static method to build UserDetails from User entity
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(), // Pass the user's password
                authorities);
    }

    // Static method to build UserDetails from Doctor entity
    public static UserDetailsImpl buildFromDoctor(Doctor doctor) {
        List<GrantedAuthority> authorities = doctor.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                doctor.getId(),
                doctor.getUsername(),
                null, // Doctor entity doesn't have an email field
                doctor.getPassword(), // Pass the doctor's password
                authorities);
    }
    public static UserDetailsImpl buildFromAdmin(Admin admin) {
        List<GrantedAuthority> authorities = admin.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                admin.getId(),
                admin.getUsername(),
                admin.getEmail(), // Admin entity has an email field
                admin.getPassword(), // Pass the admin's password
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password; // Return the stored password
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
