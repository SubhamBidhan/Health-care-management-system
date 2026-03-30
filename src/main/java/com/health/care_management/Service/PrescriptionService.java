package com.health.care_management.Service;

import com.health.care_management.Entity.Prescription;
import com.health.care_management.Repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    // Method to save a prescription
    public Prescription savePrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    // Method to find a prescription by ID
    public Optional<Prescription> findPrescriptionById(Long id) {
        return prescriptionRepository.findById(id);
    }

    // Method to delete a prescription
    public void deletePrescription(Long id) {
        prescriptionRepository.deleteById(id);
    }
    public List<Prescription> findByPatientId(Long patientId){
        return prescriptionRepository.findByPatientId(patientId);
    }
    public List<Prescription> findByDoctorId(Long DoctorId){
        return prescriptionRepository.findByDoctorId(DoctorId);
    }

    // Other service methods can be added as needed
}
