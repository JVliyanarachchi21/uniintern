package com.uniintern.portal.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class StudentApplyController {

    @GetMapping("/student/apply/{id}")
    public String apply(@PathVariable int id, Model model) {
        // Mock internship details for form header
        model.addAttribute("internship", Map.of(
                "id", id,
                "title", "Frontend Developer Intern",
                "company", "TechCorp Solutions",
                "location", "Colombo"
        ));
        return "student/apply";
    }
}