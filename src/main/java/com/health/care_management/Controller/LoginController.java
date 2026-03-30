package com.health.care_management.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.Duration;
import com.health.care_management.Dto.UserDto;
import com.health.care_management.Entity.OtpManager;
import com.health.care_management.Entity.User;
import com.health.care_management.Repository.OtpRepository;
import com.health.care_management.Service.EmailService;
import com.health.care_management.Service.OtpService;
import com.health.care_management.Service.UserService;
import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;




@Controller
public class LoginController {
    @Autowired
    private OtpService otpService;
    @Autowired
    private EmailService emailService;
     @Autowired
    private UserService userService;
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @GetMapping("/verify-otp")
    public String verifyPage(@RequestParam("username") String username, Model model) {
        model.addAttribute("username", username);
        return "Verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam("username") String username,
            @RequestParam("otp") String otp, HttpSession session,
            RedirectAttributes redirectAttributes) {

        boolean isValidOtp = otpService.verifyOtp(username, otp);
        OtpManager temp=otpRepository.findByUsername(username);
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime otpExpirationTime = temp.getExpirationTime();
        Duration duration = Duration.between(currentTime, otpExpirationTime);
        long minutesDifference = duration.toMinutes();
        if (isValidOtp && minutesDifference >0) {
            UserDto userDto = (UserDto) session.getAttribute("userDto");
            userService.registerNewUser(userDto);
            OtpManager otpManager=otpRepository.findByUsername(userDto.getUsername());
            otpRepository.delete(otpManager);
            session.removeAttribute("userDto");
            redirectAttributes.addFlashAttribute("successMessage",
                    "OTP verified successfully! You are now registered.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid or expired OTP. Please try again.");
            return "redirect:/verify-otp?username=" + username;
        }
    }
    @GetMapping("/resend-otp")
    public String resendOtp(@RequestParam("username") String username, RedirectAttributes redirectAttributes,HttpSession session) throws MessagingException {
        
        OtpManager otpManager = otpRepository.findByUsername(username);
        
        if (otpManager != null) {
            otpManager.setOtp(otpService.generateOtp());
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
            otpManager.setExpirationTime(expirationTime);
            OtpManager otp1=otpRepository.save(otpManager);
            UserDto userDto = (UserDto) session.getAttribute("userDto");
            emailService.sendOtpEmail(userDto.getEmail(), userDto.getFullName(),otp1.getOtp()); // or userDto.getPhoneNumber()

            redirectAttributes.addFlashAttribute("successMessage", "New OTP has been sent to your registered mobile number/email.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }

        return "redirect:/verify-otp?username=" + username; // Redirect back to the verify OTP page
    }
    @GetMapping("/forgot-password")
    public String forgotpassword() {
        return "forgotPasswordEmail";
    }
    @GetMapping("/verify-otp-reset")
    public String resetPasswordOtp(@RequestParam("username") String username, Model model) {
        model.addAttribute("username", username);
        return "VerifyOtpReset";
    }
    
    @PostMapping("/get-OtpForPass")
    public String sendOtpForPAss(@RequestParam("username") String username,RedirectAttributes redirectAttributes) throws MessagingException {
        Optional<User> Optionaluser=userService.findUser(username);
        if(Optionaluser.isPresent()){
            User user=Optionaluser.get();
            String email=user.getEmail();
            OtpManager otpManager = otpRepository.findByUsername(username);
        
        if (otpManager != null) {
            otpManager.setOtp(otpService.generateOtp());
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
            otpManager.setExpirationTime(expirationTime);
            OtpManager otp1=otpRepository.save(otpManager);
          emailService.sendOtpForPasswordReset(email, username,otp1.getOtp()); // or userDto.getPhoneNumber()

            redirectAttributes.addFlashAttribute("successMessage", "OTP has been sent to your registered mobile number/email.");
        }
        else{
            OtpManager otp = otpService.saveOtp(username);
            emailService.sendOtpForPasswordReset(email, username,otp.getOtp());
            redirectAttributes.addFlashAttribute("successMessage", "OTP has been sent to your registered mobile number/email.");

        }
        }
        return "redirect:/verify-otp-reset?username=" + username;
        }
    
    @PostMapping("/verify-otp-reset")
    public String VErifyOtpReset(@RequestParam("username") String username,@RequestParam("otp") String otp,RedirectAttributes redirectAttributes) {
        
        boolean isValidOtp = otpService.verifyOtp(username, otp);
        OtpManager temp=otpRepository.findByUsername(username);
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime otpExpirationTime = temp.getExpirationTime();
        Duration duration = Duration.between(currentTime, otpExpirationTime);
        long minutesDifference = duration.toMinutes();
        if (isValidOtp && minutesDifference >0) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "OTP verified successfully!");
            return "redirect:/reset-password?username="+username;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid or expired OTP. Please try again.");
            return "redirect:/login";
        }
    
    }
    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("username") String username,Model model) {
        model.addAttribute("username", username);
        return "ResetPassword";
    }
    @PostMapping("/reset-password")
    public String passwordReset(@RequestParam("username") String username,@RequestParam("newPassword") String newPassword
    ,@RequestParam("confirmPassword") String confirmPassword,
    RedirectAttributes redirectAttributes) {
        if(newPassword.equals(confirmPassword)){
            Optional<User> Optionaluser=userService.findUser(username);
            if(Optionaluser.isPresent()){
                User user=Optionaluser.get();
                user.setPassword(passwordEncoder.encode(newPassword));
                userService.save(user);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Password Changed Suscessfully");

            return "redirect:/login";
        }
        else{
            redirectAttributes.addFlashAttribute("errorMessage", "New And Confirm Password not matched");
            return "redirect:/reset-password?username="+username;
        }
       
    }
    
    
}
