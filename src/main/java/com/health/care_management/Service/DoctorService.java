package com.health.care_management.Service;

import com.health.care_management.Dto.DoctorDTO;
import com.health.care_management.Entity.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    Optional<Doctor> findById(Long id);
    public void save(Doctor doctor);
    public List<Doctor> findallDoctors();
    public void registerNewDoctor(DoctorDTO doctorDTO);
    public void deleteById(Long doctorID);
    
}
