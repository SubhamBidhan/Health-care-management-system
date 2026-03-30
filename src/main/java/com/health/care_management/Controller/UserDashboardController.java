package com.health.care_management.Controller;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.health.care_management.Entity.Admin;
import com.health.care_management.Entity.Appointment;
import com.health.care_management.Entity.Doctor;
import com.health.care_management.Entity.Prescription;
import com.health.care_management.Entity.User;
import com.health.care_management.Repository.AppointmentRepository;
import com.health.care_management.Repository.DoctorRepository;
import com.health.care_management.Repository.UserRepository;
import com.health.care_management.Service.AdminService;
import com.health.care_management.Service.AppointmentService;
import com.health.care_management.Service.DoctorService;
import com.health.care_management.Service.EmailService;
import com.health.care_management.Service.PrescriptionService;
import com.itextpdf.io.exceptions.IOException;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;





@Controller
public class UserDashboardController {

    @Autowired
    private UserRepository userRepository; // Assume you have a UserRepository for database access
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private PrescriptionService prescriptionService;
    @Autowired 
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AdminService adminService;
    


    @GetMapping("/user/dashboard")
    public String userDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get username from the SecurityContext

        Optional<User> userOptional = userRepository.findByUsername(username);
        
       
        User user = userOptional.get();
        model.addAttribute("user", user);
       
        List<Doctor> doctors = doctorService.getAllDoctors();
        int totalDoctors=doctors.size();
        List<Appointment> userAppointments = appointmentService.getAppointmentsByPatientId(user.getId());
        List <Prescription> prescription=prescriptionService.findByPatientId(user.getId());
        int totalAppointments=userAppointments.size();
        int medicalRecordsCount=prescription.size();
        model.addAttribute("totalDoctors", totalDoctors);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("appointments", userAppointments);
        model.addAttribute("medicalRecordsCount", medicalRecordsCount);
        return "UserDashboard";
    }
    @GetMapping("/user/appointment")
public String UserAppointment(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName(); // Get username from the SecurityContext

    Optional<User> userOptional = userRepository.findByUsername(username);

    User user = userOptional.get();
    System.out.println(user.getId());
    model.addAttribute("user", user);

    List<Doctor> doctors = doctorService.getAllDoctors();
    List<Appointment> userAppointments = appointmentService.getAppointmentsByPatientId(user.getId());
    model.addAttribute("doctors", doctors);
    model.addAttribute("userAppointments", userAppointments);

    System.out.println(userAppointments); // This will help you debug the fetched appointments
    return "UserAppointment"; // Return the appointment page
}

 @PostMapping("/user/appointments/create")
public String createAppointment(
        @RequestParam("doctorId") Long doctorId,
        @RequestParam("date") String date,
        @RequestParam("time") String time,
        RedirectAttributes redirectAttributes,
        @RequestParam("redirectUrl") String redirectUrl) throws MessagingException {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // Get username from the SecurityContext
        
            Optional<User> userOptional = userRepository.findByUsername(username);
            User user = userOptional.get();
            Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
            User patient = userRepository.findById(user.getId()).orElse(null); // Assuming you have a way to get the current user

    if (doctor == null || patient == null) {
        redirectAttributes.addFlashAttribute("errorMessage", "Doctor or Patient not found!");
        return "redirect:" + redirectUrl; // Redirect back to the appointment page
    }

    
    LocalDateTime appointmentDate = LocalDateTime.parse(date + "T" + time);

    Appointment appointment = new Appointment();
    appointment.setDoctor(doctor);
    

    if(doctor.getSlots()==0){
        redirectAttributes.addFlashAttribute("errorMessage", "All Slots has been Booked");
        return "redirect:" + redirectUrl;
    }
    doctor.setSlots(doctor.getSlots()-1);
    doctorService.save(doctor);
    appointment.setPatient(patient);
    appointment.setAppointmentDate(appointmentDate);
    appointment.setStatus("Scheduled"); // Default status or any logic you want


    appointmentRepository.save(appointment);
    emailService.AppointmentRequest(user.getEmail(), user.getFullName(), doctor.getName(),date, time);
    redirectAttributes.addFlashAttribute("successMessage", "Appointment created successfully with ID: " + appointment.getId());
    return "redirect:" + redirectUrl; // Redirect to the appointment page to display success message
}

@GetMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) throws MessagingException {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid appointment ID: " + id));

        // Set the appointment status to 'Cancelled'
        appointment.setStatus("Cancelled");
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElse(null);
        doctor.setSlots(doctor.getSlots()+1);
        appointmentRepository.save(appointment);

        // Add a success message to be shown on the UI
        redirectAttributes.addFlashAttribute("successMessage", "Appointment successfully cancelled.");
        emailService.sendCancellationEmail(appointment.getPatient().getEmail(), appointment.getPatient().getFullName(), doctor.getName(), appointment.getAppointmentDate());
        return "redirect:/user/appointment"; // Redirect back to the appointment page
    }
    @GetMapping("/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        
                
        appointmentRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("successMessage", "Appointment successfully cancelled.");

        return "redirect:/user/appointment"; // Redirect back to the appointment page
    }
   @GetMapping("/user/doctor")
public String Doctorpage(Model model, @ModelAttribute("successMessage") String successMessage,@ModelAttribute("errorMessage") String errorMessage) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName(); // Get username from the SecurityContext

    Optional<User> userOptional = userRepository.findByUsername(username);
    User user = userOptional.get();
    model.addAttribute("user", user);

    List<Doctor> doctors = doctorService.findallDoctors();
    model.addAttribute("doctors", doctors);

    if (successMessage != null && !successMessage.isEmpty()) {
        model.addAttribute("successMessage", successMessage);
    }
    if (errorMessage != null && !errorMessage.isEmpty()) {
        model.addAttribute("errorMessage", errorMessage);
    }
    return "UserDoctor";
}

    @GetMapping("/user/medical-records")
    public String UserMedicalRecord(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get username from the SecurityContext
    
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.get();
        List <Prescription> prescription=prescriptionService.findByPatientId(user.getId());
        model.addAttribute("prescriptions", prescription);
        return "UserMedicalRecord";
    }
    @GetMapping("/download-prescription")
public void downloadPrescription(@RequestParam Long id, HttpServletResponse response) throws java.io.IOException {
    Optional<Prescription> prescriptionOptional = prescriptionService.findPrescriptionById(id);
    if (prescriptionOptional.isPresent()) {
        Prescription prescription = prescriptionOptional.get();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + prescription.getFileName());
        
        try {
            response.getOutputStream().write(prescription.getPdfFile());
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error appropriately
        }
    } else {
        // Handle the case where the prescription is not found
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
} 
@GetMapping("/user/UserChangePassword")
public String UserChangePassword() {
    return "UserChangePassword";
}
@PostMapping("/changePassword")
public String PasswordChange(@RequestParam("password") String password,
                             @RequestParam("confirmPassword") String confirmPassword,
                             @RequestParam("redirectUrl") String redirectUrl,
                             RedirectAttributes redirectAttributes) {

    // Authentication to get the currently logged-in user/doctor
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    // Check for user or doctor based on username
    Optional<User> userOptional = userRepository.findByUsername(username);
    Optional<Doctor> doctorOptional = doctorRepository.findByUsername(username);
    Optional<Admin> adminOptional=adminService.findByUsername(username);                         
    // Check if the passwords match
    if (!password.equals(confirmPassword)) {
        redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
        return "redirect:" + redirectUrl;
    }


    if (doctorOptional.isPresent()) {
        Doctor doctor = doctorOptional.get();
        doctor.setPassword(passwordEncoder.encode(password));
        doctorRepository.save(doctor); // Save updated doctor password
    } else if (userOptional.isPresent()) {
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user); // Save updated user password
    } 
    else if (adminOptional.isPresent()) {
        Admin admin=adminOptional.get();
        admin.setPassword(passwordEncoder.encode(password));
        adminService.save(admin);
    }else {
        redirectAttributes.addFlashAttribute("error", "User/Doctor not found.");
        return "redirect:" + redirectUrl;
    }

    // Success message after password change
    redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
    return "redirect:" + redirectUrl;
}


}
