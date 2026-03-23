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

        model.addAttribute("studentName", "Ashan");
        model.addAttribute("profileCompletion", 85);
        model.addAttribute("activeApplications", 8);
        model.addAttribute("interviews", 2);
        model.addAttribute("notificationCount", 3);

        model.addAttribute("latestNotifications", List.of(
                Map.of("text", "Your application for Frontend Developer Intern has been shortlisted!", "date", "2026-02-14"),
                Map.of("text", "Interview scheduled for Full Stack Developer Intern on Feb 25.", "date", "2026-02-16"),
                Map.of("text", "New internship posted: UI/UX Design Intern at TechCorp Solutions.", "date", "2026-02-15")
        ));

        model.addAttribute("recentInternships", List.of(
                Map.of("title", "Frontend Developer Intern", "company", "TechCorp Solutions", "location", "Colombo", "deadline", "2026-04-15", "match", 82),
                Map.of("title", "Data Science Intern", "company", "DataMinds Analytics", "location", "Kandy", "deadline", "2026-03-30", "match", 74),
                Map.of("title", "IoT Developer Intern", "company", "GreenLeaf Innovations", "location", "Galle", "deadline", "2026-05-01", "match", 65)
        ));

        model.addAttribute("recentApplications", List.of(
                Map.of("title", "Frontend Developer Intern", "company", "TechCorp Solutions", "date", "2026-02-14", "status", "Shortlisted"),
                Map.of("title", "Backend Engineer Intern", "company", "FinEdge Systems", "date", "2026-02-10", "status", "Rejected"),
                Map.of("title", "Data Science Intern", "company", "DataMinds Analytics", "date", "2026-02-16", "status", "Scored")
        ));

        model.addAttribute("upcomingInterview", Map.of(
                "title", "Full Stack Developer Intern",
                "company", "TechCorp Solutions",
                "date", "2026-02-25",
                "time", "10:30 AM",
                "mode", "Online",
                "location", "Google Meet"
        ));

        model.addAttribute("activityFeed", List.of(
                Map.of("title", "Applied for UI/UX Design Intern", "date", "2026-02-12"),
                Map.of("title", "Profile updated with new skills", "date", "2026-02-13"),
                Map.of("title", "Shortlisted for Frontend Developer Intern", "date", "2026-02-14"),
                Map.of("title", "Interview scheduled by admin", "date", "2026-02-16")
        ));

        return "student/dashboard";
    }
}