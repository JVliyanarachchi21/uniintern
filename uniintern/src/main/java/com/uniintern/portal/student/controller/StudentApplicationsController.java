package com.uniintern.portal.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class StudentApplicationsController {

    @GetMapping("/student/applications")
    public String applications(Model model) {

        model.addAttribute("applications", List.of(
                Map.of("id", 101, "title", "Frontend Developer Intern", "company", "TechCorp Solutions",
                        "appliedDate", "2026-02-14", "score", 82, "status", "SHORTLISTED"),
                Map.of("id", 102, "title", "Data Science Intern", "company", "DataMinds Analytics",
                        "appliedDate", "2026-02-16", "score", 74, "status", "SCORED"),
                Map.of("id", 103, "title", "IoT Developer Intern", "company", "GreenLeaf Innovations",
                        "appliedDate", "2026-02-15", "score", 0, "status", "APPLIED"),
                Map.of("id", 104, "title", "Backend Engineer Intern", "company", "FinEdge Systems",
                        "appliedDate", "2026-02-10", "score", 58, "status", "REJECTED")
        ));

        return "student/applications";
    }
}