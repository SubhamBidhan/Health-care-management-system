package com.health.care_management.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.health.care_management.Entity.Doctor;

import java.util.Optional;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    Optional<Doctor> findByUsername(String username);
    Optional<Doctor> findById(Long id);
    Boolean existsByUsername(String username);

} 