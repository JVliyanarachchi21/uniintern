package com.uniintern.portal.student.controller;

import com.uniintern.portal.company.entity.Interview;
import com.uniintern.portal.company.repository.InterviewRepository;
import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class StudentInterviewsController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @GetMapping("/student/interviews")
    public String interviews(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) return "redirect:/student/login";

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) return "redirect:/student/login";

        Student student = studentOpt.get();
        model.addAttribute("fullName", student.getFullName());
        model.addAttribute("student", student);

        // Fetch Real Interviews from Database
        List<Interview> realInterviews = interviewRepository.findByStudentId(studentId);
        List<Map<String, Object>> formattedInterviews = new ArrayList<>();

        DateTimeFormatter dateLabelFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter timeLabelFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        for (Interview interview : realInterviews) {
            Map<String, Object> iMap = new HashMap<>();
            iMap.put("id", interview.getId());
            iMap.put("title", interview.getInternshipTitle());
            
            // Reconciling Company Info
            iMap.put("company", interview.getCompanyName() != null ? interview.getCompanyName() : "Partner Organization");
            iMap.put("logoPath", null); // Template will show first letter of company name

            if (interview.getInterviewDateTime() != null) {
                iMap.put("date", interview.getInterviewDateTime().format(dateLabelFormatter));
                iMap.put("time", interview.getInterviewDateTime().format(timeLabelFormatter));
            } else {
                iMap.put("date", "TBD");
                iMap.put("time", "TBD");
            }
            
            // Map status for template badges
            String status = interview.getStatus();
            iMap.put("status", "SCHEDULED".equalsIgnoreCase(status) ? "CONFIRMED" : status);
            
            // Restoring enhanced scheduling fields from stashed changes
            iMap.put("mode", interview.getMode() != null ? interview.getMode() : "Direct Interview");
            iMap.put("location", interview.getLocationLink() != null ? interview.getLocationLink() : "To be communicated by Admin");

            formattedInterviews.add(iMap);
        }

        model.addAttribute("interviews", formattedInterviews);
        model.addAttribute("activePage", "interviews");
        
        return "student/interviews";
    }
}
