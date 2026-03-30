package com.health.care_management.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.health.care_management.Dto.LoginRequest;
import com.health.care_management.Dto.UserDto;
import com.health.care_management.Entity.JwtResponse;
import com.health.care_management.Entity.OtpManager;
import com.health.care_management.Repository.OtpRepository;
import com.health.care_management.Service.EmailService;
import com.health.care_management.Service.OtpService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import com.health.care_management.Config.JwtProvider;

@Controller
public class AuthController {

    @Autowired
    private OtpService otpService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private JwtProvider jwtProvider;

    // Register a new user
    @PostMapping("/register/user")
    public String registerUser(
            @RequestParam String fullname,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            RedirectAttributes redirectAttributes, HttpSession httpSession) throws MessagingException {
        try {
            OtpManager otpManager = otpRepository.findByUsername(username);
            if (otpManager != null) {
                otpManager.setOtp(otpService.generateOtp());
                LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
                otpManager.setExpirationTime(expirationTime);
                otpRepository.save(otpManager);

                UserDto userDto = new UserDto();
                userDto.setFullName(fullname);
                userDto.setEmail(email);
                userDto.setContactNumber(phone);
                userDto.setPassword(password);
                userDto.setUsername(username);

                httpSession.setAttribute("userDto", userDto);
                return "redirect:/verify-otp?username=" + userDto.getUsername();
            } else {
                UserDto userDto = new UserDto();
                userDto.setFullName(fullname);
                userDto.setEmail(email);
                userDto.setContactNumber(phone);
                userDto.setPassword(password);
                userDto.setUsername(username);

                httpSession.setAttribute("userDto", userDto);

                OtpManager otp = otpService.saveOtp(username);
                
                emailService.sendOtpEmail(email, fullname, otp.getOtp());
           
                return "redirect:/verify-otp?username=" + userDto.getUsername();
            }

        } catch (RuntimeException e) {
            
            redirectAttributes.addFlashAttribute("errorMessage", "User registration failed: " + e.getMessage());
           
            return "redirect:/login";
        }
    }

    // Authenticate user and return JWT with role-based redirection hint
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest,
            RedirectAttributes redirectAttributes) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT
            String jwt = jwtProvider.generateToken(authentication.getName());

            // Get user details and roles
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Determine redirection based on role
            @SuppressWarnings("unused")
            String redirectUrl = "";
            if (roles.contains("ROLE_USER")) {
                redirectUrl = "/user/dashboard"; // User dashboard
            } else if (roles.contains("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard"; // Admin dashboard
            } else if (roles.contains("ROLE_DOCTOR")) {
                redirectUrl = "/doctor/dashboard"; // Doctor dashboard
            }

            // Return JWT, user details, roles, and the suggested redirect URL
            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "invalid username or password");
            return (ResponseEntity<?>) ResponseEntity.badRequest();
        }
    }

}
