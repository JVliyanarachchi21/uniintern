package com.uniintern.portal.student.controller;

import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.service.CompanyService;
import com.uniintern.portal.company.service.InternshipService;
import com.uniintern.portal.student.model.ApplicationStatus;
import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.model.StudentApplication;
import com.uniintern.portal.student.repository.StudentApplicationRepository;
import com.uniintern.portal.student.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class StudentApplyController {

    @Autowired
    private InternshipService internshipService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentApplicationRepository studentApplicationRepository;

    @Autowired
    private com.uniintern.portal.student.service.NotificationService notificationService;

    @GetMapping("/student/apply/{id}")
    public String apply(@PathVariable Long id, HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        
        // Path B: Anonymous User from Browse Internships -> Redirect to Register
        if (studentId == null) {
            return "redirect:/student/register?internshipId=" + id;
        }

        Internship internship = internshipService.getById(id);
        if (internship == null) return "redirect:/student/internships";

        Company company = (internship.getCompanyId() != null) ? companyService.findById(internship.getCompanyId()) : null;
        
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        studentOpt.ifPresent(student -> model.addAttribute("student", student));

        model.addAttribute("internship", internship);
        model.addAttribute("company", company);
        return "student/apply";
    }

    @PostMapping("/student/apply/{id}")
    public String submitApplication(
            @PathVariable Long id,
            @RequestParam Double gpa,
            @RequestParam String skills,
            @RequestParam String experience,
            @RequestParam String coverLetter,
            HttpSession session
    ) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");

        // IF NOT LOGGED IN -> Redirect to registration with data
        if (studentId == null) {
            return String.format("redirect:/student/register?internshipId=%d&gpa=%.2f&skills=%s&experience=%s&coverLetter=%s",
                    id, gpa, 
                    java.net.URLEncoder.encode(skills, java.nio.charset.StandardCharsets.UTF_8),
                    java.net.URLEncoder.encode(experience, java.nio.charset.StandardCharsets.UTF_8),
                    java.net.URLEncoder.encode(coverLetter, java.nio.charset.StandardCharsets.UTF_8));
        }

        // IF LOGGED IN -> Save Application
        Internship internship = internshipService.getById(id);
        Optional<Student> studentOpt = studentRepository.findById(studentId);

        if (internship != null && studentOpt.isPresent()) {
            Student student = studentOpt.get();
            
            StudentApplication application = new StudentApplication();
            application.setStudentId(studentId);
            application.setInternshipId(id);
            application.setStatus(ApplicationStatus.APPLIED);
            application.setRemarks(coverLetter);
            
            // Calculate and set match score for persistence
            double score = calculateMatchScore(student, internship);
            application.setScore(score);
            
            studentApplicationRepository.save(application);
            
            // Create notification for application submission
            notificationService.createNotification(studentId, "Application Submitted", "Your application for " + internship.getTitle() + " has been successfully submitted.", "Application");
            
            return "redirect:/student/applications";
        }

        return "redirect:/student/internships";
    }

    private double calculateMatchScore(Student student, Internship internship) {
        double s = calcSkills(student, internship);
        double g = calcGpa(student, internship);
        double e = calcExp(student, internship);
        double c = calcCert(student, internship);
        return s + g + e + c;
    }

    private int calcSkills(Student student, Internship internship) {
        Integer weightObj = internship.getSkillsWeight();
        int weight = (weightObj != null) ? weightObj : 40;
        if (internship.getRequiredSkills() == null || internship.getRequiredSkills().isEmpty()) return weight;
        if (student.getSkills() == null || student.getSkills().isEmpty()) return 0;
        Set<String> req = Arrays.stream(internship.getRequiredSkills().toLowerCase().split("[,\\s]+")).filter(s -> !s.isBlank()).collect(Collectors.toSet());
        Set<String> stud = Arrays.stream(student.getSkills().toLowerCase().split("[,\\s]+")).filter(s -> !s.isBlank()).collect(Collectors.toSet());
        long matches = req.stream().filter(stud::contains).count();
        return (int) (((double) matches / req.size()) * weight);
    }

    private int calcGpa(Student student, Internship internship) {
        Integer weightObj = internship.getGpaWeight();
        int weight = (weightObj != null) ? weightObj : 30;
        if (internship.getMinGpa() == null || internship.getMinGpa() == 0) return weight;
        if (student.getGpa() == null) return 0;
        if (student.getGpa() >= internship.getMinGpa()) return weight;
        return (int) ((student.getGpa() / internship.getMinGpa()) * weight);
    }

    private int calcExp(Student student, Internship internship) {
        Integer weightObj = internship.getExperienceWeight();
        int weight = (weightObj != null) ? weightObj : 20;
        if (student.getExperience() == null || student.getExperience().isEmpty()) return 0;
        return student.getExperience().length() > 50 ? weight : (int)((student.getExperience().length()/50.0)*weight);
    }

    private int calcCert(Student student, Internship internship) {
        Integer weightObj = internship.getCertificatesWeight();
        int weight = (weightObj != null) ? weightObj : 10;
        if (student.getCertifications() == null || student.getCertifications().isEmpty()) return 0;
        return student.getCertifications().length() > 20 ? weight : (int)((student.getCertifications().length()/20.0)*weight);
    }
}