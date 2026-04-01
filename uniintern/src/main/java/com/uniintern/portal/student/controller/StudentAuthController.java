package com.uniintern.portal.student.controller;

import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.model.StudentStatus;
import com.uniintern.portal.student.repository.StudentRepository;
import com.uniintern.portal.student.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Controller
public class StudentAuthController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmailService emailService;

    // A simple in-memory cache for OTPs mapped by email (for prototyping purposes)
    private final Map<String, String> otpStorage = new HashMap<>();

    @GetMapping("/student/register")
    public String registerPage() {
        return "student/register";
    }

    @PostMapping("/student/register")
    public String handleRegister(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String university,
            @RequestParam String degreeProgram,
            @RequestParam String regNo,
            @RequestParam String nicNumber,
            @RequestParam String gpa,
            @RequestParam(required = false) String terms,
            Model model
    ) {

        if (!email.matches("(?i)^[a-z]{2}\\d{8}@my\\.sliit\\.lk$")) {
            model.addAttribute("error", "Email must be a valid SLIIT student email (e.g., IT23123456@my.sliit.lk)");
            return "student/register";
        }

        if (studentRepository.existsByEmail(email)) {
            // For testing prototyping, we will allow you to overwrite your old mock registration
            // so you don't get stuck on "Email already registered" when testing the OTP flow!
            Student existing = studentRepository.findByEmail(email).get();
            studentRepository.delete(existing);
            studentRepository.flush();
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "student/register";
        }

        Student student = new Student();
        student.setFullName(name);
        student.setEmail(email);
        student.setPassword(password);
        student.setUniversity(university);
        student.setDegreeProgram(degreeProgram);
        student.setRegistrationNumber(regNo);
        student.setNicNumber(nicNumber);
        
        try {
            student.setGpa(Double.parseDouble(gpa));
        } catch (NumberFormatException e) {
            student.setGpa(0.0);
        }

        student.setAcademicYear("Not Set");
        student.setSkills("");
        student.setExperience("");
        student.setCertifications("");
        student.setCvFilePath("");

        studentRepository.save(student);

        // Generate 6-digit random OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Store the OTP
        otpStorage.put(email, otp);

        try {
            // Send the OTP via the EmailService to their university email
            emailService.sendOtpEmail(email, otp);
            System.out.println("====== OTP EMAIL DISPATCHED TO " + email + " ======");
        } catch (Exception e) {
            System.out.println("Failed to send OTP email (Check SMTP settings): " + e.getMessage());
            System.out.println("====== (FALLBACK) OTP FOR " + email + " is " + otp + " ======");
        }

        return "redirect:/student/verify-otp?email=" + email;
    }

    @GetMapping("/student/verify-otp")
    public String verifyOtpPage(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "student/verify-otp";
    }

    @PostMapping("/student/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, HttpSession session, Model model) {

        if (otp == null || !otp.matches("^\\d{6}$")) {
            model.addAttribute("email", email);
            model.addAttribute("otpError", "OTP must contain exactly 6 digits");
            return "student/verify-otp";
        }

        // Verify the OTP against our in-memory storage
        String storedOtp = otpStorage.get(email);
        
        if (storedOtp == null) {
            model.addAttribute("email", email);
            model.addAttribute("otpError", "OTP session expired or not found. Please register again.");
            return "student/verify-otp";
        }

        if (!storedOtp.equals(otp)) {
            model.addAttribute("email", email);
            model.addAttribute("otpError", "Invalid OTP code. Please try again.");
            return "student/verify-otp";
        }

        // Verification successful, activate the account and remove OTP from memory
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setStatus(StudentStatus.VERIFIED);
            studentRepository.save(student);
            
            // Set student in session
            session.setAttribute("loggedInStudentId", student.getId());
        }

        otpStorage.remove(email);

        return "redirect:/student/dashboard";
    }
}