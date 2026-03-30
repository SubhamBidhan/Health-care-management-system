package com.health.care_management.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.health.care_management.Entity.Appointment;

import java.time.LocalDate;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    @Query("SELECT a.appointmentDate FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate >= :startDate ")
    List<String> findBookedSlots(@Param("doctorId") Long doctorId, @Param("startDate") LocalDate startDate);
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND DATE(a.appointmentDate) = CURRENT_DATE AND a.status = 'Scheduled'")
    List<Appointment> findAppointmentsByDoctorIdAndToday(@Param("doctorId") Long doctorId);
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate >= CURRENT_DATE AND a.status = 'Scheduled'")
    List<Appointment> findUpcomingAppointmentsByDoctorId(@Param("doctorId") Long doctorId);

    

}
