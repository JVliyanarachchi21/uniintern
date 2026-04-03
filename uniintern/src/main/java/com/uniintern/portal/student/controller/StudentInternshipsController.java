package com.uniintern.portal.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class StudentInternshipsController {

    private final com.uniintern.portal.company.service.InternshipService internshipService;

    public StudentInternshipsController(com.uniintern.portal.company.service.InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    @GetMapping("/student/internships")
    public String internships(Model model) {

        // Filters (mock defaults)
        model.addAttribute("selectedLocation", "All");
        model.addAttribute("selectedType", "All");
        model.addAttribute("matchMin", 50);

        // Real internship cards from database
        List<com.uniintern.portal.company.dto.InternshipListingDto> internships = internshipService.getApprovedInternshipsListings();
        model.addAttribute("internships", internships);

        return "student/internships";
    }
}