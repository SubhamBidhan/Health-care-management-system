package com.health.care_management.Controller;

import java.util.ArrayList;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.health.care_management.Entity.Appointment;
import com.health.care_management.Entity.Doctor;
import com.health.care_management.Entity.Prescription;
import com.health.care_management.Entity.User;
import com.health.care_management.Repository.DoctorRepository;
import com.health.care_management.Service.AppointmentService;
import com.health.care_management.Service.EmailService;
import com.health.care_management.Service.PrescriptionService;
import com.health.care_management.Service.UserService;

import jakarta.mail.MessagingException;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;






@Controller
public class DoctorDashboardController {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private PrescriptionService prescriptionService;
  

    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get username from the SecurityContext

        Optional<Doctor> doctorOptional = doctorRepository.findByUsername(username);
        
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            model.addAttribute("doctor", doctor); // Add doctor to the model
            
            // Fetch appointments for the doctor
            List<Appointment> appointments = appointmentService.getFutureAppointmentsByDoctorId(doctor.getId());
            model.addAttribute("appointments", appointments);
            List<User> uniquePatients = new ArrayList<>();
            for (Appointment appointment : appointments) {
                if(!uniquePatients.contains(appointment.getPatient())){
                    uniquePatients.add(appointment.getPatient());
                }
                
            }
            int totalPatients=uniquePatients.size();
            model.addAttribute("totalPatients",totalPatients);

            List<Prescription> prescriptions=prescriptionService.findByDoctorId(doctor.getId());
            int totalPrescriptions=prescriptions.size();
            
            model.addAttribute("totalPrescription",totalPrescriptions);
            
            int totalAppointments = appointments.size();
            model.addAttribute("totalAppointments", totalAppointments);
        } else {
            // Handle case where doctor is not found (optional)
            model.addAttribute("error", "Doctor not found");
        }

        return "DoctorDashboard"; // Thymeleaf template name
    }

    @GetMapping("/doctor/appointments")
    public String DoctorAppointment(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get username from the SecurityContext

        Optional<Doctor> doctorOptional = doctorRepository.findByUsername(username);
        
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            Long doctorId = doctor.getId();

            // Fetch all appointments for the doctor
            List<Appointment> appointments = appointmentService.getFutureAppointmentsByDoctorId(doctorId);

            // Add appointments to the model to pass to the view
            model.addAttribute("appointments", appointments);

           
            List<Appointment> todaysAppointments = appointmentService.findtodayAppointments(doctorId);

            System.out.println(todaysAppointments);
            System.out.println(appointments);
            // Add today's appointments to the model to pass to the view
            model.addAttribute("todaysAppointments", todaysAppointments);
        }
        
        return "DoctorAppointment";
    }

    @GetMapping("/doctor/Prescription")
    public String DoctorPrescription(Model model,RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get username from the SecurityContext

        Optional<Doctor> doctorOptional = doctorRepository.findByUsername(username);
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            Long doctorId = doctor.getId();

            // Fetch all appointments for the doctor
            List<Appointment> appointments = appointmentService.getFutureAppointmentsByDoctorId(doctorId);
            
            // Add appointments to the model to pass to the view
            model.addAttribute("appointments", appointments);

            // Create a map to hold patientId and username
            Map<Long, String> patientMap = new HashMap<>(); // Correctly instantiate the Map

            
            for (Appointment appointment : appointments) {
                User user = appointment.getPatient(); 

                Optional<User> patientOptional = userService.findById(user.getId());
                if (patientOptional.isPresent()) {
                    User patient = patientOptional.get();
                    patientMap.put(patient.getId(), patient.getUsername());
                }
            }

            
            model.addAttribute("patientMap", patientMap);
            System.out.println(patientMap);
            List<Appointment> todaysAppointments = appointmentService.findtodayAppointments(doctorId);
           
            model.addAttribute("todaysAppointments", todaysAppointments);
        }
        return "DoctorPescribtion"; // Ensure this matches your Thymeleaf template
    }
    @PostMapping("/doctor/appointments/{id}/complete")

    public ResponseEntity<Void> completeAppointment(@PathVariable Long id) {
        Optional<Appointment> appointmentOptional = appointmentService.getAppointmentById(id);
        
        if (appointmentOptional.isPresent()) {
            Appointment appointment = appointmentOptional.get();
            appointment.setStatus("Completed"); // Update the status
            appointmentService.saveAppointment(appointment); // Save the updated appointment
            return ResponseEntity.ok().build(); // Return 200 OK
        }
        
        return ResponseEntity.notFound().build(); // Return 404 Not Found if appointment not found
    }
    @GetMapping("/doctor/passwordChange")
    public String passwordChnage() {
        return "DoctorPasswordChange";
    }
    @GetMapping("/doctor/patients")
    public String PatientList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get username from the SecurityContext
        Optional<Doctor> doctorOptional = doctorRepository.findByUsername(username);
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            List<Appointment> appointments = appointmentService.getFutureAppointmentsByDoctorId(doctor.getId());
            List<User> uniquePatients = new ArrayList<>();
            for (Appointment appointment : appointments) {
                if(!uniquePatients.contains(appointment.getPatient())){
                    uniquePatients.add(appointment.getPatient());

                }
                
        }
        model.addAttribute("patients",uniquePatients);
            }
        return "DoctorPatientList";
    }
    @PostMapping("/doctor/appointments/reschedule")
public String rescheduleAppointment(@RequestParam("appointmentId") Long appointmentId,
                                    @RequestParam("newDate") String newDate,
                                    @RequestParam("newTime") String newTime) {
    Appointment appointment = appointmentService.getAppointmentById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

    // Parse the time from the request
    LocalDateTime newAppointmentDateTime = LocalDateTime.parse(newDate + "T" + newTime);

    // Update the appointment date and time
    appointment.setAppointmentDate(newAppointmentDateTime);
    appointmentService.saveAppointment(appointment);

    return "redirect:/doctor/appointments";  // Redirect to the appointments page after rescheduling
}

    @GetMapping("/doctor/meet")
    public String Meeting(@RequestParam(required = false) Long appointmentId, Model model) {
        // Check if appointmentId is present and print it to the terminal
        Optional<Appointment> appointmentOptional=appointmentService.getAppointmentById(appointmentId);
        if (appointmentOptional.isPresent()) {
            Appointment appointment=appointmentOptional.get();
            Doctor doctor=appointment.getDoctor();
            model.addAttribute("doctorName", doctor.getName());
        }
        if (appointmentId != null) {
            System.out.println("Meeting requested for appointment ID: " + appointmentId);
        } else {
            System.out.println("Meeting requested without an appointment ID.");
        }

        
        return "meet"; // Ensure this matches your Thymeleaf template
    }
  
    @GetMapping("doctor/ScheduledMeet/{appointmentId}")
public ResponseEntity<String> ScheduledMeet(@PathVariable Long appointmentId, @RequestParam String time) throws MessagingException {
    Optional<Appointment> appointmentOptional = appointmentService.getAppointmentById(appointmentId);
    if (appointmentOptional.isPresent()) {
        Appointment appointment = appointmentOptional.get();
        Doctor doctor = appointment.getDoctor();
        User patient = appointment.getPatient();

        String patientName = patient.getFullName();
        String doctorName = doctor.getName();
        String userEmail = patient.getEmail();
        String meetLink = "http://localhost:8080/doctor/meet?appointmentId=" + appointmentId;

        // Pass the doctor and patient names, time, and meet link to the email service
        emailService.sendMeetingScheduledEmail(userEmail, patientName, doctorName, String.valueOf(appointmentId), time, meetLink);
        
        return ResponseEntity.ok("Meeting scheduled and email sent successfully");
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
    }
}

    
    
}
