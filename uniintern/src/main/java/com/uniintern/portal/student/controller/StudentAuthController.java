package com.uniintern.portal.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudentAuthController {

    @GetMapping("/register/student")
    public String registerPage() {
        return "student/register";
    }

    @PostMapping("/register/student")
    public String handleRegister(@RequestParam String email, Model model) {

        // UI phase → pretend email is valid
        model.addAttribute("email", email);

        return "student/verify-otp";
    }

    @PostMapping("/verify-otp/student")
    public String verifyOtp(@RequestParam String otp) {

        // UI phase → accept any OTP
        return "redirect:/student/dashboard";
    }
}