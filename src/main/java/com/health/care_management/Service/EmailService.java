package com.health.care_management.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMeetingScheduledEmail(String toEmail, String patientName, String doctorName, String appointmentId,
            String time, String meetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Appointment Confirmation - Aditya TeleHealth Care");
        helper.setFrom("am9982061@gmail.com");

        String emailContent = "<p>Dear " + patientName + ",</p>" +
                "<p>We hope this message finds you well.</p>" +
                "<p>We are pleased to inform you that your appointment request has been successfully scheduled. Please find the details of your appointment below:</p>"
                +
                "<p><strong>Appointment ID:</strong> " + appointmentId + "<br>" +
                "<strong>Doctor:</strong> " + doctorName + "<br>" +
                "<strong>Scheduled Time:</strong> " + time + "<br>" +
                "<strong>Meeting Link:</strong> <a href=\"" + meetLink + "\">Join Meeting</a></p>" +
                "<p>Please make sure to join the meeting using the above link at the scheduled time. Kindly note that the appointment time may vary by up to 10 minutes before or after the specified time, so we request you to be prepared accordingly.</p>"
                +
                "<p>We truly value your trust in our services, and we are committed to providing you with the best possible care. If you have any questions or need assistance, feel free to reach out to us.</p>"
                +
                "<p>With gratitude,</p>" +
                "<p><strong>Best regards,</strong><br>" +
                "Aditya TeleHealth Care<br>" +
                "Phone: +91-8114756133<br>" +
                "Email: support@adityatelehealth.com</p>" +
                "<p><em>Note: Please do not share this meeting link with anyone else for privacy and security reasons.</em></p>";

        helper.setText(emailContent, true);
        mailSender.send(message);
    }

    public void AppointmentRequest(String toEmail, String patientName, String doctorName, String date, String time)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set email recipient, subject, and sender
        helper.setTo(toEmail);
        helper.setSubject("Appointment Request Received - Aditya TeleHealth Care");
        helper.setFrom("am9982061@gmail.com");

        // Email content with respectful and informative language
        String emailContent = "<p>Dear " + patientName + ",</p>" +
                "<p>We have received your appointment request with " + doctorName + " on <strong>" + date
                + "</strong> at <strong>" + time + "</strong>.</p>" +
                "<p>We will make every effort to schedule your appointment for the requested time. However, in case our doctor is occupied, we will arrange the appointment within one hour of the specified time. Please keep an eye on your email for updates, and we will send you the meeting link very soon.</p>"
                +
                "<p>We truly appreciate your patience and understanding.</p>" +
                "<p>With gratitude,</p>" +
                "<p><strong>Best regards,</strong><br>" +
                "Aditya TeleHealth Care<br>" +
                "Phone: +91-8114756133<br>" +
                "Email: support@adityatelehealth.com</p>" +
                "<p><em>Note: Please do not share this meeting link with anyone else for privacy and security reasons.</em></p>";

        // Set email content as HTML
        helper.setText(emailContent, true);

        // Send the email
        mailSender.send(message);
    }

    public void sendCancellationEmail(String toEmail, String patientName, String doctorName,
            LocalDateTime appointmentDate) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set email recipient, subject, and sender
        helper.setTo(toEmail);
        helper.setSubject("Appointment Cancellation - Aditya TeleHealth Care");
        helper.setFrom("am9982061@gmail.com");

        // Format the appointment date and time for email
        String formattedDate = appointmentDate.toLocalDate().toString();
        String formattedTime = appointmentDate.toLocalTime().toString();

        // Email content for appointment cancellation
        String emailContent = "<p>Dear " + patientName + ",</p>" +
                "<p>We regret to inform you that your scheduled appointment with  " + doctorName + " on <strong>"
                + formattedDate + "</strong> at <strong>" + formattedTime + "</strong> has been cancelled.</p>" +
                "<p>We apologize for any inconvenience this may cause. If you would like to reschedule, please visit our portal or contact our support team for assistance.</p>"
                +
                "<p>If you have any questions or need help rescheduling, feel free to reach out to us at any time.</p>"
                +
                "<p>With gratitude,</p>" +
                "<p><strong>Best regards,</strong><br>" +
                "Aditya TeleHealth Care<br>" +
                "Phone: +91-8114756133<br>" +
                "Email: support@adityatelehealth.com</p>";

        // Set email content as HTML
        helper.setText(emailContent, true);

        // Send the email
        mailSender.send(message);
    }

    public void sendPrescriptionEmail(String toEmail, String patientName, String doctorName, String fileName,
            byte[] pdfFile) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Your Prescription - Aditya TeleHealth Care");
        helper.setFrom("am9982061@gmail.com");

        String emailContent = "<p>Dear " + patientName + ",</p>" +
                "<p>We hope this message finds you in good health.</p>" +
                "<p>We are pleased to inform you that the prescription from your recent consultation with  "
                + doctorName + " has been successfully generated.</p>" +
                "<p><strong>Please find your prescription attached to this email.</strong></p>" +
                "<p>You can also access and download your prescription anytime by logging into your account on our website</p>"
                +
                "<p><strong>Prescription Details:</strong><br>" +
                "Doctor: Dr. " + doctorName + "<br>" +
                "Prescription ID: " + fileName + "</p>" +
                "<p>If you have any further questions or need assistance, feel free to reach out to us at your convenience.</p>"
                +
                "<p>We are dedicated to providing you with the best possible care, and we appreciate your trust in Aditya TeleHealth Care.</p>"
                +
                "<p>Thank you for choosing us for your healthcare needs.</p>" +
                "<p>With sincere gratitude,</p>" +
                "<p><strong>Best regards,</strong><br>" +
                "Aditya TeleHealth Care<br>" +
                "Phone: +91-8114756133<br>" +
                "Email: support@adityatelehealth.com</p>" +
                "<p><em>Note: Please do not share your prescription with anyone to ensure privacy and confidentiality.</em></p>";

        // Set email content as HTML
        helper.setText(emailContent, true);

        // Attach the prescription PDF
        helper.addAttachment(fileName, new ByteArrayDataSource(pdfFile, "application/pdf"));

        // Send the email
        mailSender.send(message);
    }

    public void sendOtpEmail(String toEmail, String patientName, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Your OTP Code - Aditya TeleHealth Care");
        helper.setFrom("am9982061@gmail.com");

        String emailContent = "<p>Dear " + patientName + ",</p>" +
                "<p>Thank you for choosing Aditya TeleHealth Care. Please find your One-Time Password (OTP) below:</p>" +
                "<p><strong>Your OTP is: " + otp + "</strong></p>" +
                "<p>Please use this OTP to verify your identity. This OTP is valid for a limited time, so please use it promptly.</p>" +
                "<p>If you did not request this OTP, please ignore this email.</p>" +
                "<p>With gratitude,</p>" +
                "<p><strong>Best regards,</strong><br>" +
                "Aditya TeleHealth Care<br>" +
                "Phone: +91-8114756133<br>" +
                "Email: support@adityatelehealth.com</p>" +
                "<p><em>Note: This OTP is confidential and should not be shared with anyone.</em></p>";

        helper.setText(emailContent, true);
        mailSender.send(message);
    }
    public void sendOtpForPasswordReset(String toEmail, String username, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    
        helper.setTo(toEmail);
        helper.setSubject("Password Reset OTP - Aditya TeleHealth Care");
        helper.setFrom("am9982061@gmail.com");
    
        String emailContent = "<p>Dear " + username + ",</p>" +
                "<p>We received a request to reset the password for your account at Aditya TeleHealth Care.</p>" +
                "<p>Please use the following One-Time Password (OTP) to reset your password:</p>" +
                "<p><strong>Your OTP is: " + otp + "</strong></p>" +
                "<p>This OTP is valid for a limited time and should be used promptly.</p>" +
                "<p>If you did not request a password reset, please ignore this email or contact our support team.</p>" +
                "<p>With gratitude,</p>" +
                "<p><strong>Best regards,</strong><br>" +
                "Aditya TeleHealth Care<br>" +
                "Phone: +91-8114756133<br>" +
                "Email: support@adityatelehealth.com</p>" +
                "<p><em>Note: This OTP is confidential and should not be shared with anyone.</em></p>";
    
        helper.setText(emailContent, true);
            mailSender.send(message);
    }
    
}
