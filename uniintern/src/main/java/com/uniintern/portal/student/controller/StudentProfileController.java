package com.uniintern.portal.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StudentProfileController {

    @GetMapping("/student/profile")
    public String profile(Model model) {

        // Mock student info
        model.addAttribute("fullName", "Ashan Perera");
        model.addAttribute("email", "ashan@uni.edu");
        model.addAttribute("university", "SLIIT");
        model.addAttribute("degree", "BSc (Hons) IT");
        model.addAttribute("regNo", "IT20231234");
        model.addAttribute("nic", "200112345678");
        model.addAttribute("gpa", "3.25");

        // Mock skill tags (UI phase)
        model.addAttribute("skills", List.of("Java", "Spring Boot", "PostgreSQL", "Git"));

        return "student/profile";
    }
}