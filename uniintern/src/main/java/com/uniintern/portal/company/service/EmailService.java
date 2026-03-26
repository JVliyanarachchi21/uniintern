package com.uniintern.portal.company.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationOtp(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("UniIntern - Verify Your Company Email");
        message.setText("Welcome to UniIntern!\n\nYour OTP for company email verification is: " + otp + "\n\nThis OTP will expire in 15 minutes.");
        try {
            javaMailSender.send(message);
            System.out.println("OTP Email sent to " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
        }
    }
}
