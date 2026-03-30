package com.health.care_management.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.health.care_management.Dto.DoctorDTO;
import com.health.care_management.Dto.UserDto;
import com.health.care_management.Entity.Appointment;
import com.health.care_management.Entity.Doctor;
import com.health.care_management.Entity.Prescription;
import com.health.care_management.Entity.User;
import com.health.care_management.Service.AppointmentService;
import com.health.care_management.Service.DoctorService;
import com.health.care_management.Service.PrescriptionService;
import com.health.care_management.Service.UserService;



@Controller
public class AdminDashboardController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private PrescriptionService prescriptionService;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        List<User> users = userService.findAllUsers();
        int allusers = users.size();
    
        List<Doctor> doctors = doctorService.findallDoctors();
        int allDoctors = doctors.size();
    
        List<Appointment> appointments = appointmentService.findAllAppointments();
        int totalAppointments = appointments.size();
    
        // Get monthly appointment counts
        Map<String, Integer> monthlyCounts = appointmentService.getMonthlyAppointmentsCount();
        System.out.println(monthlyCounts);
        System.out.println(allDoctors);
        System.out.println(allusers);
        System.out.println(totalAppointments);
        model.addAttribute("monthlyCounts", monthlyCounts);
    
        model.addAttribute("allDoctors", allDoctors);
        model.addAttribute("allusers", allusers);
        model.addAttribute("totalAppointments", totalAppointments);
    
        return "AdminDashboard";
    }
    

    @GetMapping("/admin/allusers")
    public String AdminAllUsers(Model model) {
        List<User> Allusers = userService.findAllUsers();
        model.addAttribute("Allusers", Allusers);
        return "AdminUserPage";
    }
    @PostMapping("/admin/user/edit")
    public String editUser(
            @RequestParam("userId") Long userId,
            @RequestParam("editfullName") String fullName,
            @RequestParam("editEmail") String email,
            @RequestParam("editPhone") String phone,
            @RequestParam(value = "password", required = false) String password, // Capture password if provided
            RedirectAttributes redirectAttributes) {
    
        Optional<User> Optionaluser = userService.findById(userId);
        if (Optionaluser.isPresent()) {
            User user = Optionaluser.get();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setContactNumber(phone);
    
            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }
    
            userService.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            return "redirect:/admin/allusers";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/admin/allusers";
        }
    }
    

    @PostMapping("/admin/addUser")
    public String AddnewUser(@RequestParam("username") String username,
            @RequestParam("fullname") String fullname,
            @RequestParam("email") String email,
            @RequestParam("contact") String contact,
            RedirectAttributes redirectAttributes) {
                try{
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setFullName(fullname);
        userDto.setEmail(email);
        userDto.setContactNumber(contact);
        userDto.setPassword(username);
        userService.registerNewUser(userDto);
        redirectAttributes.addFlashAttribute("successMessage", "User Registered successfully!");
        return "redirect:/admin/allusers";
    }
                catch (RuntimeException e) {
                    // Set error message
                    redirectAttributes.addFlashAttribute("errorMessage", "User registration failed: " + e.getMessage());
                    return "redirect:/admin/allusers";
                }
        

    }

    @PostMapping("/admin/user/delete")
    public String deleteUser(@RequestParam("userId") Long userId, RedirectAttributes redirectAttributes) {
        // Call the service to delete the user by ID
        if (userService.findById(userId).isPresent()) {
            List<Appointment> appointment = appointmentService.getAppointmentsByPatientId(userId);
            for (Appointment a : appointment) {
                appointmentService.deleteAppointment(a.getId());
            }
            List<Prescription> prescriptions = prescriptionService.findByPatientId(userId);
            for (Prescription p : prescriptions) {
                prescriptionService.deletePrescription(p.getId());
            }
            userService.deleteById(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin/allusers"; // Redirect to the user list page
    }

    @GetMapping("/admin/allDoctors")
    public String allDoctors(Model model) {
        List<Doctor> doctors = doctorService.findallDoctors();
        model.addAttribute("doctors", doctors);
        return "AdminDoctorList";
    }

    @PostMapping("/admin/doctor/edit")
    public String editDoctor(
            @RequestParam("editDoctorId") Long doctorId,
            @RequestParam("editDoctorName") String doctorName,
            @RequestParam("editDoctorExperience") String doctorExperience,
            @RequestParam("editDoctorSpecialization") String doctorSpecialization,
            @RequestParam("editCountryCode") String countryCode,
            @RequestParam("editDoctorPhone") String doctorPhone,
            @RequestParam(value = "password", required = false) String password, 
            RedirectAttributes redirectAttributes) {
    
        String fullPhoneNumber = countryCode + " " + doctorPhone;
    
        Optional<Doctor> optionalDoctor = doctorService.findById(doctorId);
        if (!optionalDoctor.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Doctor not found.");
            return "redirect:/admin/allDoctors";
        } else {
            Doctor doctor = optionalDoctor.get();
            doctor.setName(doctorName);
            doctor.setExperience(doctorExperience);
            doctor.setSpecialization(doctorSpecialization);
            doctor.setContactNumber(fullPhoneNumber);
    
            // Update password only if provided
            if (password != null && !password.isEmpty()) {
                doctor.setPassword(passwordEncoder.encode(password));
            }
    
            doctorService.save(doctor);
            redirectAttributes.addFlashAttribute("successMessage", "Doctor updated successfully!");
            return "redirect:/admin/allDoctors";
        }
    }
    

    @PostMapping("/admin/doctor/add")
    public String addDoctor(
            @RequestParam("doctorUserName") String doctorUserName,
            @RequestParam("doctorName") String doctorName,
            @RequestParam("doctorExperience") String doctorExperience,
            @RequestParam("doctorSpecialization") String doctorSpecialization,
            @RequestParam("doctorPhone") String doctorPhone,
            @RequestParam("countryCode") String countryCode,
            RedirectAttributes redirectAttributes) {

        try {
            // Combine country code and phone number
            String fullPhoneNumber = countryCode + doctorPhone;

            // Create a new doctor object and save it
            DoctorDTO doctor = new DoctorDTO();
            doctor.setDoctorUserName(doctorUserName);
            doctor.setDoctorName(doctorName);
            doctor.setDoctorExperience(doctorExperience);
            doctor.setDoctorSpecialization(doctorSpecialization);
            doctor.setDoctorPhone(fullPhoneNumber);

            // Save the doctor using the service
            doctorService.registerNewDoctor(doctor);

            // Add success message
            redirectAttributes.addFlashAttribute("successMessage", "Doctor added successfully!");
        } catch (Exception e) {
            // Handle any errors and add error message
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding doctor: " + e.getMessage());
        }

        return "redirect:/admin/allDoctors";
    }

    @PostMapping("/admin/doctor/delete")
    public String DoctorDelete(@RequestParam("doctorId") Long doctorId,
            RedirectAttributes redirectAttributes) {

        if (doctorService.findById(doctorId).isPresent()) {
            List<Appointment> appointment = appointmentService.getAppointmentsbyDoctorId(doctorId);
            for (Appointment a : appointment) {
                appointmentService.deleteAppointment(a.getId());
            }
            List<Prescription> prescriptions = prescriptionService.findByDoctorId(doctorId);
            for (Prescription p : prescriptions) {
                prescriptionService.deletePrescription(p.getId());
            }
            doctorService.deleteById(doctorId);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin/allDoctors";
    }
   @GetMapping("/admin/AppointmentList")
public String AdminAppointmentList( Model model) {
    
    List<Doctor> doctors = doctorService.findallDoctors();

 
    Map<Doctor, Map<String, Integer>> appointmentCounts = new HashMap<>();

    for (Doctor doctor : doctors) {
      
        List<Appointment> appointments = appointmentService.getAppointmentsAllByDoctorId(doctor.getId());

       
        int receivedCount = appointments.size(); 
        int completedCount = (int) appointments.stream()
            .filter(appointment -> "Completed".equals(appointment.getStatus())) 
            .count();

   
        Map<String, Integer> counts = new HashMap<>();
        counts.put("received", receivedCount);
        counts.put("completed", completedCount);

        
        appointmentCounts.put(doctor, counts);
    }

   
    model.addAttribute("appointmentCounts", appointmentCounts);

    return "AdminApoointmentList"; 
}
    @GetMapping("/admin/changePassword")
    public String changePassword(Model model) {
        return "AdminChnagePassword";
    }
    

}
