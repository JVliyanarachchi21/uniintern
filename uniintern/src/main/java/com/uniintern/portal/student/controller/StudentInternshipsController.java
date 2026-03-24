package com.uniintern.portal.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class StudentInternshipsController {

    @GetMapping("/student/internships")
    public String internships(Model model) {

        // Filters (mock defaults)
        model.addAttribute("selectedLocation", "All");
        model.addAttribute("selectedType", "All");
        model.addAttribute("matchMin", 50);

        // Mock internship cards
        model.addAttribute("internships", List.of(
                Map.of("id", 1, "title", "Frontend Developer Intern", "company", "TechCorp Solutions", "location", "Colombo",
                        "duration", "6 months", "match", 82, "deadline", "2026-04-15", "skills", List.of("React", "JS", "UI")),
                Map.of("id", 2, "title", "Data Science Intern", "company", "DataMinds Analytics", "location", "Kandy",
                        "duration", "3 months", "match", 74, "deadline", "2026-03-30", "skills", List.of("Python", "ML", "SQL")),
                Map.of("id", 3, "title", "IoT Developer Intern", "company", "GreenLeaf Innovations", "location", "Galle",
                        "duration", "6 months", "match", 65, "deadline", "2026-05-01", "skills", List.of("C", "IoT", "Sensors")),
                Map.of("id", 4, "title", "Backend Engineer Intern", "company", "FinEdge Systems", "location", "Colombo",
                        "duration", "6 months", "match", 58, "deadline", "2026-04-20", "skills", List.of("Java", "Spring", "Postgres"))
        ));

        return "student/internships";
    }
}