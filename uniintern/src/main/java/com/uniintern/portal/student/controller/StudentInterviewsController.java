package com.uniintern.portal.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class StudentInterviewsController {

    @GetMapping("/student/interviews")
    public String interviews(Model model) {

        model.addAttribute("interviews", List.of(
                Map.of("title", "Frontend Developer Intern", "company", "TechCorp Solutions",
                        "date", "2026-02-25", "time", "10:30 AM", "mode", "Online",
                        "location", "Meet link: https://meet.google.com/xxx-xxxx-xxx", "status", "PENDING"),
                Map.of("title", "Data Science Intern", "company", "DataMinds Analytics",
                        "date", "2026-02-28", "time", "02:00 PM", "mode", "Onsite",
                        "location", "Colombo HQ - Level 5", "status", "CONFIRMED")
        ));

        return "student/interviews";
    }
}
