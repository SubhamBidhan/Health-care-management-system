package com.health.care_management.Service.impl;

import com.health.care_management.Dto.DoctorDTO;
import com.health.care_management.Entity.Doctor;
import com.health.care_management.Entity.Role;
import com.health.care_management.Repository.DoctorRepository;
import com.health.care_management.Repository.RoleRepository;
import com.health.care_management.Service.DoctorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public void save(Doctor doctor) {
        doctorRepository.save(doctor);
    }
    @Override
    public void deleteById(Long doctorID) {
        doctorRepository.deleteById(doctorID);
    }

    @Override
    public Optional<Doctor> findById(Long id) {
        return doctorRepository.findById(id);
    }

    @Override
    public List<Doctor> findallDoctors() {
        return doctorRepository.findAll();
    }

    public void registerNewDoctor(DoctorDTO doctorDTO) {
        // Check if the user already exists
        if (doctorRepository.existsByUsername(doctorDTO.getDoctorUserName())) {
            throw new RuntimeException("Username already exists!");
        }

        // Hash the password
        String encodedPassword = passwordEncoder.encode(doctorDTO.getDoctorUserName());

        // Create the new doctor
        Doctor doctor = new Doctor();
        doctor.setUsername(doctorDTO.getDoctorUserName());
        doctor.setPassword(encodedPassword);
        doctor.setName(doctorDTO.getDoctorName());
        doctor.setContactNumber(doctorDTO.getDoctorPhone());
        doctor.setSpecialization(doctorDTO.getDoctorSpecialization());
        doctor.setExperience(doctorDTO.getDoctorExperience());
        doctor.setSlots(10); // Assuming slots are provided in the DTO

        // Assign default role (e.g., ROLE_USER)
        Role role = roleRepository.findByName("ROLE_DOCTOR")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Initialize roles and assign the default role
        Set<Role> roles = new HashSet<>();
        roles.add(role); // Add the default ROLE_USER
        doctor.setRoles(roles); // Set the doctor's roles

        // Save the doctor
        doctorRepository.save(doctor);
    }

}
