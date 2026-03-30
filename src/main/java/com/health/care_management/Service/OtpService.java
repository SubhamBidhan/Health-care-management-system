package com.health.care_management.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.health.care_management.Entity.OtpManager;
import com.health.care_management.Repository.OtpRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    public String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    public OtpManager saveOtp(String username) {
        String otpCode = generateOtp();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5); 
        OtpManager otpManager = new OtpManager();
        otpManager.setUsername(username);
        otpManager.setOtp(otpCode);
        otpManager.setExpirationTime(expirationTime);
        
        return otpRepository.save(otpManager);
    }

    
    public boolean verifyOtp(String username, String inputOtp) {
        OtpManager storedOtp = otpRepository.findByUsername(username); 
        
        if (storedOtp == null) {
            return false; 
        }
        return storedOtp.getOtp().equals(inputOtp) && LocalDateTime.now().isBefore(storedOtp.getExpirationTime());
    }
}
