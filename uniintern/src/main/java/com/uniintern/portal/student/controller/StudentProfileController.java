package com.uniintern.portal.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StudentProfileController {

    @GetMapping("/student/profile")
    public String profile(Model model) {

        // Basic student info is now provided globally via loggedInStudent

        // Mock skill tags (UI phase)
        model.addAttribute("skills", List.of("Java", "Spring Boot", "PostgreSQL", "Git"));

        return "student/profile";
    }
}