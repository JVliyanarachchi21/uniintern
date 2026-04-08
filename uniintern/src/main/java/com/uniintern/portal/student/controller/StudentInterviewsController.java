package com.uniintern.portal.student.controller;

import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class StudentInterviewsController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/student/interviews")
    public String interviews(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) return "redirect:/student/login";

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) return "redirect:/student/login";

        Student student = studentOpt.get();
        model.addAttribute("fullName", student.getFullName());
        model.addAttribute("student", student);

        // Professional Mock Data for Demonstration
        List<Map<String, Object>> mockInterviews = new ArrayList<>();

        // Interview 1: Pending
        Map<String, Object> i1 = new HashMap<>();
        i1.put("id", 101L);
        i1.put("title", "UI/UX Design Intern");
        i1.put("company", "Global Finance Bank");
        i1.put("logoPath", "/images/companies/gfb-logo.png");
        i1.put("date", "April 15, 2026");
        i1.put("time", "10:30 AM");
        i1.put("mode", "Online (Zoom)");
        i1.put("location", "https://zoom.us/j/123456789");
        i1.put("status", "PENDING_CONFIRMATION");
        mockInterviews.add(i1);

        // Interview 2: Confirmed
        Map<String, Object> i2 = new HashMap<>();
        i2.put("id", 102L);
        i2.put("title", "Frontend Developer Intern");
        i2.put("company", "TechVision Solutions");
        i2.put("logoPath", "/images/companies/techvision-logo.png");
        i2.put("date", "April 18, 2026");
        i2.put("time", "02:00 PM");
        i2.put("mode", "In-Person");
        i2.put("location", "Level 12, Tech Tower, Colombo 03");
        i2.put("status", "CONFIRMED");
        mockInterviews.add(i2);

        model.addAttribute("interviews", mockInterviews);
        model.addAttribute("activePage", "interviews");

        return "student/interviews";
    }
}
