package com.uniintern.portal.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Controller
public class StudentApplicationDetailController {

    @GetMapping("/student/applications/{id}")
    public String detail(@PathVariable int id, Model model) {

        // Mock details (UI phase)
        model.addAttribute("applicationId", id);
        model.addAttribute("title", "Frontend Developer Intern");
        model.addAttribute("company", "TechCorp Solutions");
        model.addAttribute("status", "SHORTLISTED");
        model.addAttribute("score", 82);

        model.addAttribute("timeline", List.of(
                Map.of("step", "Applied", "date", "2026-02-14", "done", true),
                Map.of("step", "Scored", "date", "2026-02-16", "done", true),
                Map.of("step", "Shortlisted", "date", "2026-02-18", "done", true),
                Map.of("step", "Interview Scheduled", "date", "—", "done", false),
                Map.of("step", "Completed", "date", "—", "done", false)
        ));

        return "student/application-detail";
    }
}