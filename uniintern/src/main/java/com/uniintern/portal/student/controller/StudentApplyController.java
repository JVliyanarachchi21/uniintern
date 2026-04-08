package com.uniintern.portal.student.controller;

import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.service.CompanyService;
import com.uniintern.portal.company.service.InternshipService;
import com.uniintern.portal.student.model.ApplicationScore;
import com.uniintern.portal.student.model.ApplicationStatus;
import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.model.StudentApplication;
import com.uniintern.portal.student.repository.ApplicationScoreRepository;
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

    @Autowired
    private ApplicationScoreRepository applicationScoreRepository;

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
        if (studentOpt.isEmpty()) return "redirect:/student/login";
        
        Student student = studentOpt.get();
        model.addAttribute("student", student);
        model.addAttribute("fullName", student.getFullName());
        model.addAttribute("internship", internship);
        model.addAttribute("company", company);
        
        // Calculate match score for the header card
        int skills = calcSkills(student, internship);
        int gpa = calcGpa(student, internship);
        int exp = calcExp(student, internship);
        int cert = calcCert(student, internship);
        model.addAttribute("match", skills + gpa + exp + cert);
        
        // Format skills for display
        if (internship.getRequiredSkills() != null) {
            model.addAttribute("skills", Arrays.asList(internship.getRequiredSkills().split("[,\\s]+")));
        }
        
        return "student/apply";
    }

    @PostMapping("/student/apply/{id}")
    public String submitApplication(
            @PathVariable Long id,
            @RequestParam Double gpa,
            @RequestParam String skills,
            @RequestParam String experience,
            @RequestParam String coverLetter,
            @RequestParam("cvFile") org.springframework.web.multipart.MultipartFile cvFile,
            HttpSession session
    ) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");

        if (studentId == null) {
            return "redirect:/student/login"; // Simplified for logged-in students
        }

        Internship internship = internshipService.getById(id);
        Optional<Student> studentOpt = studentRepository.findById(studentId);

        if (internship != null && studentOpt.isPresent()) {
            Student student = studentOpt.get();
            
            StudentApplication application = new StudentApplication();
            application.setStudentId(studentId);
            application.setInternshipId(id);
            application.setStatus(ApplicationStatus.APPLIED);
            application.setRemarks(coverLetter);
            
            // Handle CV File Upload
            if (cvFile != null && !cvFile.isEmpty()) {
                try {
                    String uploadDir = "uploads/applications/cvs/";
                    java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
                    if (!java.nio.file.Files.exists(uploadPath)) {
                        java.nio.file.Files.createDirectories(uploadPath);
                    }
                    
                    String fileName = java.util.UUID.randomUUID().toString() + "_" + cvFile.getOriginalFilename();
                    java.nio.file.Path filePath = uploadPath.resolve(fileName);
                    java.nio.file.Files.copy(cvFile.getInputStream(), filePath);
                    
                    application.setCvFilePath("/" + uploadDir + fileName);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
            
            double score = calculateMatchScore(student, internship);
            application.setScore(score);
            
            studentApplicationRepository.save(application);
            saveScoreBreakdown(application.getId(), student, internship);
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

    private void saveScoreBreakdown(Long appId, Student student, Internship internship) {
        // Skills
        int skillsWeight = internship.getSkillsWeight() != null ? internship.getSkillsWeight() : 40;
        String skillsCriteria = internship.getRequiredSkills() != null ? internship.getRequiredSkills() : "Not Specified";
        String skillsData = student.getSkills() != null ? student.getSkills() : "None";
        int skillsScore = calcSkills(student, internship);
        applicationScoreRepository.save(new ApplicationScore(appId, "Skills", skillsWeight, skillsCriteria, skillsData, skillsScore));

        // GPA
        int gpaWeight = internship.getGpaWeight() != null ? internship.getGpaWeight() : 30;
        String gpaCriteria = "Min " + (internship.getMinGpa() != null ? internship.getMinGpa() : "0.0");
        String gpaData = student.getGpa() != null ? student.getGpa().toString() : "0.0";
        int gpaScore = calcGpa(student, internship);
        applicationScoreRepository.save(new ApplicationScore(appId, "GPA", gpaWeight, gpaCriteria, gpaData, gpaScore));

        // Experience
        int expWeight = internship.getExperienceWeight() != null ? internship.getExperienceWeight() : 20;
        String expCriteria = "Relevant Projects";
        String expData = (student.getExperience() != null && !student.getExperience().isEmpty()) ? "Provided" : "Not Provided";
        int expScore = calcExp(student, internship);
        applicationScoreRepository.save(new ApplicationScore(appId, "Experience", expWeight, expCriteria, expData, expScore));

        // Certificates
        int certWeight = internship.getCertificatesWeight() != null ? internship.getCertificatesWeight() : 10;
        String certCriteria = "Recognized Certs";
        String certData = (student.getCertifications() != null && !student.getCertifications().isEmpty()) ? "Provided" : "Not Provided";
        int certScore = calcCert(student, internship);
        applicationScoreRepository.save(new ApplicationScore(appId, "Certificates", certWeight, certCriteria, certData, certScore));
    }
}