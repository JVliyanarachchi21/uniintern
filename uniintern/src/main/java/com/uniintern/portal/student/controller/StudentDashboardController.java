package com.uniintern.portal.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class StudentDashboardController {

    @GetMapping("/student/dashboard")
    public String dashboard(Model model) {

        // Mock summary cards
        model.addAttribute("studentName", "Ashan");
        model.addAttribute("profileCompletion", 85);
        model.addAttribute("activeApplications", 8);
        model.addAttribute("interviews", 2);
        model.addAttribute("notificationCount", 3);

        // Mock notifications
        model.addAttribute("latestNotifications", List.of(
                Map.of("text", "Your application for Frontend Developer Intern has been shortlisted!", "date", "2026-02-14"),
                Map.of("text", "Interview scheduled for Full Stack Developer Intern on Feb 25.", "date", "2026-02-16"),
                Map.of("text", "New internship posted: UI/UX Design Intern at TechCorp Solutions.", "date", "2026-02-15")
        ));

        // Mock recent internships
        model.addAttribute("recentInternships", List.of(
                Map.of("title", "Frontend Developer Intern", "company", "TechCorp Solutions", "location", "Colombo", "deadline", "2026-04-15"),
                Map.of("title", "Data Science Intern", "company", "DataMinds Analytics", "location", "Kandy", "deadline", "2026-03-30"),
                Map.of("title", "IoT Developer Intern", "company", "GreenLeaf Innovations", "location", "Galle", "deadline", "2026-05-01"),
                Map.of("title", "Backend Engineer Intern", "company", "FinEdge Systems", "location", "Colombo", "deadline", "2026-04-20")
        ));

        return "student/dashboard";
    }
}