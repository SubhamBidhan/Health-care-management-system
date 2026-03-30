package com.health.care_management.Controller;

import com.health.care_management.Entity.Doctor;
import com.health.care_management.Entity.User;
import com.health.care_management.Entity.Prescription; // Import the Prescription entity
import com.health.care_management.Repository.DoctorRepository;
import com.health.care_management.Repository.PrescriptionRepository; // Import the PrescriptionRepository
import com.health.care_management.Service.ChamberService;
import com.health.care_management.Service.EmailService;
import com.health.care_management.Service.UserService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class PrescriptionController {

    @Autowired
    private ChamberService chamberService;

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PrescriptionRepository prescriptionRepository; // Inject the repository

    @PostMapping("/generate-chamber-pdf")
    public String generateChamberPDF(@RequestParam Long patientId,
                                   @RequestParam int patientAge,
                                   @RequestParam String patientSex,
                                   @RequestParam String diseases,
                                   @RequestParam String clinicalTest,
                                   @RequestParam String medicines,
                                   @RequestParam String additionalAdvice,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletResponse response) throws java.io.IOException, MessagingException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
    
            Optional<Doctor> doctorOptional = doctorRepository.findByUsername(username);
            String doctorName = ""; 
            String doctorQualifications = ""; 
            String appointmentContact = ""; 
            Long doctorId = null;
    
            if (doctorOptional.isPresent()) {
                Doctor doctor = doctorOptional.get();
                doctorName = doctor.getName();
                doctorQualifications = doctor.getSpecialization();
                appointmentContact = doctor.getContactNumber();
                doctorId = doctor.getId(); // Get the doctor's ID
            }
    
            String hospitalName = "Aditya Virtual Clinic";
            String hospitalAddress = "At/Po-Gunthuni" + "\nState: Odisha\nPin:-752068";
    
            Optional<User> userOptional = userService.findById(patientId);
            String patientFullName = "";
            String patientEmail = "";
    
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                patientFullName = user.getFullName();
                patientEmail = user.getEmail(); // Get the patient's email
            }
    
            // Generate the PDF and store it in a custom object
            ChamberService.ChamberPDFResult pdfResult = chamberService.generateChamberPDF(
                    patientFullName, patientAge, patientSex, doctorName, doctorQualifications,
                    hospitalName, appointmentContact, hospitalAddress, diseases, clinicalTest, medicines, additionalAdvice);
    
            // Save the PDF in the database
            Prescription prescription = new Prescription();
            prescription.setDiseases(diseases);
            prescription.setPatientId(patientId);
            prescription.setPatientName(patientFullName);
            prescription.setPdfFile(pdfResult.getPdfData()); // Store the PDF byte array
            prescription.setFileName(pdfResult.getFileName()); // Store the file name
            prescription.setDoctorId(doctorId); // Store the doctor's ID
            prescription.setDoctorName(doctorName); // Store the doctor's name
            prescriptionRepository.save(prescription); // Save the prescription
    
            // Send the prescription via email
            emailService.sendPrescriptionEmail(patientEmail, patientFullName, doctorName, pdfResult.getFileName(), pdfResult.getPdfData());
               redirectAttributes.addFlashAttribute("successMessage","Prescription generated and sent via email successfully.");
               return "redirect:/doctor/Prescription";
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error appropriately
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating or sending PDF");
            return "redirect:/doctor/Prescription";
        }
    }
    
}
