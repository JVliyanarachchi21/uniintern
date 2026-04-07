package com.uniintern.portal.student.controller;

import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class StudentProfileController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private com.uniintern.portal.student.repository.StudentApplicationRepository studentApplicationRepository;

    @Autowired
    private com.uniintern.portal.company.service.InternshipService internshipService;

    @Autowired
    private com.uniintern.portal.company.service.CompanyService companyService;

    private static final String UPLOAD_DIR = "uploads/cv/";

    @GetMapping("/student/profile")
    public String profile(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) return "redirect:/student/login";

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) return "redirect:/student/login";

        model.addAttribute("student", studentOpt.get());
        
        // Mock default skills if empty for UI phase
        if (studentOpt.get().getSkills() == null || studentOpt.get().getSkills().isEmpty()) {
            model.addAttribute("mockSkills", List.of("Java", "Spring Boot", "PostgreSQL", "Git"));
        }

        return "student/profile";
    }

    @PostMapping("/student/profile/update")
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam String degreeProgram,
            @RequestParam Double gpa,
            @RequestParam String skills,
            @RequestParam String experience,
            @RequestParam String certifications,
            @RequestParam("cvFile") MultipartFile cvFile,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) return "redirect:/student/login";

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setFullName(fullName);
            student.setDegreeProgram(degreeProgram);
            student.setGpa(gpa);
            student.setSkills(skills);
            student.setExperience(experience);
            student.setCertifications(certifications);

            // Handle CV File Upload
            if (!cvFile.isEmpty()) {
                try {
                    Path uploadRoot = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadRoot)) Files.createDirectories(uploadRoot);

                    String fileName = UUID.randomUUID().toString() + "_" + cvFile.getOriginalFilename();
                    Path filePath = uploadRoot.resolve(fileName);
                    Files.copy(cvFile.getInputStream(), filePath);
                    
                    student.setCvFilePath("/" + UPLOAD_DIR + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            studentRepository.save(student);
            redirectAttributes.addFlashAttribute("success", "Profile Saved Successfully!");
        }

        return "redirect:/student/profile";
    }

    @GetMapping("/student/applications")
    public String myApplications(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) return "redirect:/student/login";

        java.util.List<com.uniintern.portal.student.model.StudentApplication> apps = studentApplicationRepository.findByStudentId(studentId);
        // Sort by appliedAt DESC
        apps.sort((a, b) -> {
            if (a.getAppliedAt() == null) return 1;
            if (b.getAppliedAt() == null) return -1;
            return b.getAppliedAt().compareTo(a.getAppliedAt());
        });
        
        java.util.List<java.util.Map<String, Object>> displayApps = new java.util.ArrayList<>();

        for (com.uniintern.portal.student.model.StudentApplication app : apps) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", app.getId());
            map.put("status", app.getStatus());
            map.put("appliedAt", app.getAppliedAt());

            com.uniintern.portal.company.entity.Internship internship = internshipService.getById(app.getInternshipId());
            if (internship != null) {
                map.put("title", internship.getTitle());
                com.uniintern.portal.company.entity.Company company = companyService.findById(internship.getCompanyId());
                map.put("companyName", company != null ? company.getCompanyName() : "Unknown Company");
                map.put("logoPath", company != null ? company.getLogoPath() : null);
            } else {
                map.put("title", "Unknown Internship");
                map.put("companyName", "N/A");
            }
            displayApps.add(map);
        }

        model.addAttribute("applications", displayApps);
        model.addAttribute("activePage", "applications");
        return "student/my-applications";
    }
}