package com.uniintern.portal.student.controller;
  
import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.service.CompanyService;
import com.uniintern.portal.company.service.InternshipService;
import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.model.StudentApplication;
import com.uniintern.portal.student.repository.StudentApplicationRepository;
import com.uniintern.portal.student.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class StudentInternshipsController {

    @Autowired
    private InternshipService internshipService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentApplicationRepository studentApplicationRepository;

    @GetMapping("/student/internships")
    public String internships(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) return "redirect:/student/login";

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) return "redirect:/student/login";
        
        Student student = studentOpt.get();
        List<Internship> allInternships = internshipService.getAll();
        List<StudentApplication> studentApplications = studentApplicationRepository.findByStudentId(studentId);
        
        Map<Long, String> applicationStatusMap = studentApplications.stream()
                .collect(Collectors.toMap(
                    StudentApplication::getInternshipId, 
                    app -> app.getStatus().toString(),
                    (existing, replacement) -> existing
                ));

        List<Map<String, Object>> displayInternships = new ArrayList<>();
        
        for (Internship internship : allInternships) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", internship.getId());
            map.put("title", internship.getTitle());
            map.put("location", internship.getLocation() != null ? internship.getLocation() : "Remote");
            map.put("deadline", internship.getDeadline() != null ? internship.getDeadline().toString().substring(0, 10) : "N/A");
            map.put("duration", internship.getDuration() != null ? internship.getDuration() : "6 Months");
            
            Company company = (internship.getCompanyId() != null) ? companyService.findById(internship.getCompanyId()) : null;
            map.put("company", company != null ? company.getCompanyName() : "Unknown Company");
            map.put("logoPath", company != null ? company.getLogoPath() : null);
            
            // Description snippet
            String desc = internship.getDescription();
            if (desc != null && desc.length() > 120) {
                map.put("descriptionSnippet", desc.substring(0, 117) + "...");
            } else {
                map.put("descriptionSnippet", desc != null ? desc : "No description available.");
            }
            
            int score = calculateMatchScore(student, internship);
            map.put("match", score);
            
            if (internship.getRequiredSkills() != null) {
                map.put("skills", Arrays.asList(internship.getRequiredSkills().split("[,\\s]+")));
            } else {
                map.put("skills", List.of());
            }
            
            map.put("isApplied", applicationStatusMap.containsKey(internship.getId()));
            displayInternships.add(map);
        }

        model.addAttribute("internships", displayInternships);
        return "student/internships";
    }

    @GetMapping("/student/internships/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) return "redirect:/student/login";

        Internship internship = internshipService.getById(id);
        if (internship == null) return "redirect:/student/internships";

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) return "redirect:/student/login";

        Student student = studentOpt.get();
        Company company = (internship.getCompanyId() != null) ? companyService.findById(internship.getCompanyId()) : null;

        // Matching Analysis
        int skills = calcSkills(student, internship);
        int gpa = calcGpa(student, internship);
        int exp = calcExp(student, internship);
        int cert = calcCert(student, internship);

        model.addAttribute("internship", internship);
        model.addAttribute("student", student);
        model.addAttribute("company", company);
        model.addAttribute("skillsMatch", skills);
        model.addAttribute("gpaMatch", gpa);
        model.addAttribute("expMatch", exp);
        model.addAttribute("certMatch", cert);
        model.addAttribute("totalMatch", skills + gpa + exp + cert);

        // Check if already applied
        List<StudentApplication> apps = studentApplicationRepository.findByStudentId(studentId);
        boolean applied = apps.stream().anyMatch(a -> a.getInternshipId().equals(id));
        model.addAttribute("isApplied", applied);

        return "student/internship-preview";
    }

    private int calculateMatchScore(Student student, Internship internship) {
        return calcSkills(student, internship) + calcGpa(student, internship) + calcExp(student, internship) + calcCert(student, internship);
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