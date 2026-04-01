package com.uniintern.portal.student.controller;

import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudentAuthController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/student/register")
    public String registerPage() {
        return "student/register";
    }

    @PostMapping("/student/register")
    public String handleRegister(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String university,
            @RequestParam String regNo,
            Model model
    ) {

        if (studentRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email already registered");
            return "student/register";
        }

        Student student = new Student();
        student.setFullName(name);
        student.setEmail(email);
        student.setPassword(password);
        student.setUniversity(university);
        student.setDegreeProgram("Not Set");
        student.setAcademicYear("Not Set");
        student.setGpa(0.0);
        student.setSkills("");
        student.setExperience("");
        student.setCertifications("");
        student.setCvFilePath("");

        studentRepository.save(student);

        model.addAttribute("email", email);
        return "student/verify-otp";
    }

    @GetMapping("/student/verify-otp")
    public String verifyOtpPage(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "student/verify-otp";
    }

    @PostMapping("/student/verify-otp")
    public String verifyOtp(@RequestParam String otp, Model model) {

        if (otp == null || !otp.matches("^\\d{6}$")) {
            model.addAttribute("email", "");
            model.addAttribute("otpError", "OTP must contain exactly 6 digits");
            return "student/verify-otp";
        }

        return "redirect:/student/dashboard";
    }
}