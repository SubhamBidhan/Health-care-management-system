package com.health.care_management.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.health.care_management.Entity.Appointment;

import com.health.care_management.Repository.AppointmentRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.time.Month;


@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        List<Appointment> demo=appointmentRepository.findByPatientId(patientId);
        System.out.println(demo);
        return demo;
    }

    public List<Appointment> getAppointmentsbyDoctorId(Long doctorId){
        return appointmentRepository.findByDoctorId(doctorId);
    }
    public List<Appointment> getAppointmentsAllByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
    public List<Appointment> getFutureAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findUpcomingAppointmentsByDoctorId(doctorId);
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> findtodayAppointments(Long doctorId) {
            return appointmentRepository.findAppointmentsByDoctorIdAndToday(doctorId);
    }
    public List<Appointment> findAllAppointments(){
        return appointmentRepository.findAll();
    }
     
    public Map<String, Integer> getMonthlyAppointmentsCount() {
        List<Appointment> appointments = appointmentRepository.findAll();
        Map<String, Integer> monthlyCounts = new LinkedHashMap<>(); // LinkedHashMap preserves insertion order
    
        // Initialize map with all months and a count of 0
        for (Month month : Month.values()) {
            monthlyCounts.put(month.name(), 0);
        }
    
        // Iterate over appointments and update the count for each month
        for (Appointment appointment : appointments) {
            String month = appointment.getAppointmentDate().toLocalDate().getMonth().name();
            monthlyCounts.put(month, monthlyCounts.get(month) + 1);
        }
    
        return monthlyCounts;
    }
    public void deleteById(Long id){
        appointmentRepository.deleteById(id);
    }
}
