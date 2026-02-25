package com.uniintern.portal.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class StudentNotificationsController {

    @GetMapping("/student/notifications")
    public String notifications(Model model) {

        model.addAttribute("notifications", List.of(
                Map.of("type", "Application", "text", "Your application for Frontend Developer Intern has been shortlisted!", "date", "2026-02-18", "read", false),
                Map.of("type", "Interview", "text", "Interview scheduled on 2026-02-25 at 10:30 AM (Online).", "date", "2026-02-19", "read", false),
                Map.of("type", "Internship", "text", "New internship posted: UI/UX Design Intern at TechCorp Solutions.", "date", "2026-02-15", "read", true)
        ));

        return "student/notifications";
    }
}