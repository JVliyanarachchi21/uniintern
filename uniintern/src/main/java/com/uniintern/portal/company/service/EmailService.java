package com.uniintern.portal.company.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("companyEmailService")
public class EmailService {

    private final JavaMailSender javaMailSender;


    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationOtp(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
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

    public void sendApprovalNotification(String toEmail, String companyName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("UniIntern - Your Account is Approved!");
        message.setText("Congratulations " + companyName + "!\n\nYour recruiter account on UniIntern has been approved oleh admin. You can now log in to post internships and manage applicants.\n\nLogin here: http://localhost:8089/company/login\n\nBest regards,\nThe UniIntern Team");
        try {
            javaMailSender.send(message);
            System.out.println("Approval Notification Email sent to " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send approval email to " + toEmail + ": " + e.getMessage());
        }
    }

    public void sendPasswordResetOtp(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("UniIntern - Reset Your Password");
        message.setText("We received a request to reset your UniIntern recruiter password.\n\nYour OTP for password reset is: " + otp + "\n\nThis OTP will expire in 15 minutes. If you did not request this, please ignore this email.");
        try {
            javaMailSender.send(message);
            System.out.println("Password Reset OTP Email sent to " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email to " + toEmail + ": " + e.getMessage());
        }
    }

}
