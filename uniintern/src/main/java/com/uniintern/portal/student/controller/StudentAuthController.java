package com.uniintern.portal.student.controller;

import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.model.StudentStatus;
import com.uniintern.portal.student.repository.StudentRepository;
import com.uniintern.portal.student.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    private com.uniintern.portal.student.repository.StudentApplicationRepository studentApplicationRepository;

    @Autowired
    private com.uniintern.portal.student.service.NotificationService notificationService;

    // A simple in-memory cache for OTPs mapped by email (for prototyping purposes)
    private final Map<String, String> otpStorage = new HashMap<>();

    @GetMapping("/student/register")
    public String registerPage(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String university,
            @RequestParam(required = false) String degree,
            @RequestParam(required = false) String gpa,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) Long internshipId,
            @RequestParam(required = false) String coverLetter,
            Model model
    ) {
        String fullName = (firstName != null ? firstName : "") + (lastName != null ? " " + lastName : "");
        model.addAttribute("name", fullName.trim());
        model.addAttribute("email", email);
        model.addAttribute("university", university);
        model.addAttribute("degreeProgram", degree);
        model.addAttribute("gpa", gpa);
        model.addAttribute("skills", skills);
        model.addAttribute("experience", experience);
        model.addAttribute("internshipId", internshipId);
        model.addAttribute("coverLetter", coverLetter);
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
            @RequestParam(required = false) String gpa,
            @RequestParam(required = false) String terms,
            @RequestParam(required = false) String preSkills,
            @RequestParam(required = false) String preExperience,
            @RequestParam(required = false) Long internshipId,
            @RequestParam(required = false) String coverLetter,
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
            if (gpa != null && !gpa.trim().isEmpty()) {
                student.setGpa(Double.parseDouble(gpa));
            } else {
                student.setGpa(0.0);
            }
        } catch (NumberFormatException | NullPointerException e) {
            student.setGpa(0.0);
        }

        student.setAcademicYear("Not Set");
        student.setSkills(preSkills != null ? preSkills : "");
        student.setExperience(preExperience != null ? preExperience : "");
        student.setCertifications("");
        student.setCvFilePath("");

        try {
            Student savedStudent = studentRepository.save(student);
            
            // IF APPLYING VIA INTERNSHIP BROWSE
            if (internshipId != null) {
                com.uniintern.portal.student.model.StudentApplication application = new com.uniintern.portal.student.model.StudentApplication();
                application.setStudentId(savedStudent.getId());
                application.setInternshipId(internshipId);
                application.setRemarks(coverLetter);
                application.setStatus(com.uniintern.portal.student.model.ApplicationStatus.APPLIED);
                studentApplicationRepository.save(application);
                
                // Create notification for application submission
                notificationService.createNotification(savedStudent.getId(), "Application Submitted", "Your application has been successfully submitted.", "Application");
            }

        } catch (Exception e) {
            System.err.println("REGISTRATION FATAL ERROR: [ " + e.getClass().getSimpleName() + " ] - " + e.getMessage());
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

        if (internshipId != null) {
            return "redirect:/student/verify-otp?email=" + email + "&otp=" + otp + "&target=applications";
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
    public String verifyOtpPage(@RequestParam(required = false) String email, 
                                @RequestParam(required = false) String otp, 
                                @RequestParam(required = false) String target,
                                Model model) {
        model.addAttribute("email", email);
        model.addAttribute("otpHint", otp);
        model.addAttribute("target", target);
        return "student/verify-otp";
    }

    @PostMapping("/student/verify-otp")
    public String verifyOtp(@RequestParam String email, 
                            @RequestParam String otp, 
                            @RequestParam(required = false) String target,
                            HttpSession session, 
                            RedirectAttributes redirectAttributes,
                            Model model) {

        if (otp == null || !otp.matches("^\\d{6}$")) {
            model.addAttribute("email", email);
            model.addAttribute("target", target);
            model.addAttribute("otpError", "OTP must contain exactly 6 digits");
            return "student/verify-otp";
        }

        // Verify the OTP against our in-memory storage
        String storedOtp = otpStorage.get(email);
        
        if (storedOtp == null) {
            model.addAttribute("email", email);
            model.addAttribute("target", target);
            model.addAttribute("otpError", "OTP session expired or not found. Please register again.");
            return "student/verify-otp";
        }

        if (!storedOtp.equals(otp)) {
            model.addAttribute("email", email);
            model.addAttribute("target", target);
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

            // Create notification for account verification
            notificationService.createNotification(student.getId(), "Account Verified", "Welcome to UniIntern! Your account has been successfully verified.", "Account");
        }

        otpStorage.remove(email);
        
        redirectAttributes.addFlashAttribute("success", "Your account has been verified successfully. Please sign in to continue.");
        return "redirect:/student/login";
    }
}