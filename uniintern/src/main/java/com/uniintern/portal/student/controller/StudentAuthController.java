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
    @org.springframework.beans.factory.annotation.Qualifier("studentEmailService")
    private EmailService emailService;

    // A simple in-memory cache for OTPs mapped by email (for prototyping purposes)
    private final Map<String, String> otpStorage = new HashMap<>();

    @GetMapping("/student/register")
    public String registerPage() {
        return "student/register";
    }

    @GetMapping("/student/login")
    public String loginPage(Model model) {
        return "student/login";
    }

    @PostMapping("/student/login")
    public String handleLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);

        if (studentOpt.isEmpty()) {
            model.addAttribute("error", "Invalid email or password");
            model.addAttribute("email", email);
            return "student/login";
        }

        Student student = studentOpt.get();

        if (!student.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid email or password");
            model.addAttribute("email", email);
            return "student/login";
        }

        // Check if verified (unless we want to allow login during prototyping)
        // if (student.getStatus() != StudentStatus.VERIFIED) {
        //     return "redirect:/student/verify-otp?email=" + email;
        // }

        session.setAttribute("loggedInStudentId", student.getId());
        return "redirect:/student/dashboard";
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

        // 1. Validate Email Format
        if (!email.matches("(?i)^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
            model.addAttribute("error", "Please enter a valid email address.");
            addRegistrationFieldsToModel(model, name, email, university, degreeProgram, regNo, nicNumber, gpa);
            return "student/register";
        }

        // 2. Check if Email Already Exists (Properly)
        if (studentRepository.existsByEmail(email)) {
            model.addAttribute("error", "This email is already registered. Please sign in or use a different email.");
            addRegistrationFieldsToModel(model, name, email, university, degreeProgram, regNo, nicNumber, gpa);
            return "student/register";
        }

        // 3. Password Match Check
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            addRegistrationFieldsToModel(model, name, email, university, degreeProgram, regNo, nicNumber, gpa);
            return "student/register";
        }

        // 4. Create and Save Student
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

        try {
            studentRepository.save(student);
        } catch (Exception e) {
            model.addAttribute("error", "Database error: " + e.getMessage());
            addRegistrationFieldsToModel(model, name, email, university, degreeProgram, regNo, nicNumber, gpa);
            return "student/register";
        }

        // 5. Generate and Store OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp);

        try {
            emailService.sendOtpEmail(email, otp);
            System.out.println("====== OTP EMAIL DISPATCHED TO " + email + " ======");
        } catch (Exception e) {
            System.out.println("Failed to send OTP email: " + e.getMessage());
            System.out.println("====== (FALLBACK) OTP FOR " + email + " is " + otp + " ======");
        }

        return "redirect:/student/verify-otp?email=" + email + "&otp=" + otp;
    }

    private void addRegistrationFieldsToModel(Model model, String name, String email, String university, 
                                             String degreeProgram, String regNo, String nicNumber, String gpa) {
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("university", university);
        model.addAttribute("degreeProgram", degreeProgram);
        model.addAttribute("regNo", regNo);
        model.addAttribute("nicNumber", nicNumber);
        model.addAttribute("gpa", gpa);
    }

    @GetMapping("/student/verify-otp")
    public String verifyOtpPage(@RequestParam(required = false) String email, @RequestParam(required = false) String otp, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("otpHint", otp);
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