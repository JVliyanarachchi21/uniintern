package com.uniintern.portal.student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("studentEmailService")
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your UniIntern Verification Code");
        message.setText("Welcome to UniIntern!\n\nYour 6-digit registration verification code is: " + otp + "\n\nPlease enter this code to complete your registration.\n\nBest regards,\nThe UniIntern Team");
        
        mailSender.send(message);
    }
}
