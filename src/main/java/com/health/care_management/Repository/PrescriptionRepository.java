package com.health.care_management.Repository;

import com.health.care_management.Entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    // You can add custom query methods here if needed
    List<Prescription> findByPatientId(Long patientId);
    List<Prescription> findByDoctorId(Long doctorId);

}
