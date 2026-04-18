package com.uniintern.portal.student.controller;
  
import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.service.CompanyService;
import com.uniintern.portal.company.service.InternshipService;
import com.uniintern.portal.student.model.ApplicationScore;
import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.model.StudentApplication;
import com.uniintern.portal.student.repository.ApplicationScoreRepository;
import com.uniintern.portal.student.repository.StudentApplicationRepository;
import com.uniintern.portal.student.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class StudentApplicationDetailController {

    @Autowired
    private StudentApplicationRepository studentApplicationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InternshipService internshipService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ApplicationScoreRepository applicationScoreRepository;

    @GetMapping("/student/applications/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        
        System.out.println(">>> DEBUG: View Details Clicked. App ID: " + id + ", Session Student ID: " + studentId);

        if (studentId == null) {
            System.out.println(">>> DEBUG: Redirection to login - studentId is null in session");
            return "redirect:/student/login";
        }

        Optional<StudentApplication> appOpt = studentApplicationRepository.findById(id);
        if (appOpt.isEmpty()) {
            System.out.println(">>> DEBUG: Redirection - Application NOT FOUND for ID: " + id);
            return "redirect:/student/applications";
        }
        
        StudentApplication app = appOpt.get();
        if (!app.getStudentId().equals(studentId)) {
            System.out.println(">>> DEBUG: Redirection - Application " + id + " does NOT belong to student " + studentId + " (Owner: " + app.getStudentId() + ")");
            return "redirect:/student/applications";
        }

        Internship internship = internshipService.getById(app.getInternshipId());
        Optional<Student> studentOpt = studentRepository.findById(studentId);

        if (internship == null || studentOpt.isEmpty()) {
            System.out.println(">>> DEBUG: Redirection - Internship or Student record missing");
            return "redirect:/student/applications";
        }

        Student student = studentOpt.get();
        
        // HYPER-VERBOSE DIAGNOSTIC LOGGING
        System.out.println(">>> DEBUG: VIEW-DETAILS DATA SYNC CHECK");
        System.out.println(">>> App ID: " + app.getId());
        System.out.println(">>> Internship ID: " + app.getInternshipId());
        
        if (internship != null) {
            System.out.println(">>> Intern Title: [" + internship.getTitle() + "]");
            System.out.println(">>> Intern Skills Req: [" + internship.getRequiredSkills() + "]");
            System.out.println(">>> Intern GPA Req: [" + internship.getMinGpa() + "]");
            System.out.println(">>> Weights: Skills[" + internship.getSkillsWeight() + "], GPA[" + internship.getGpaWeight() + "]");
        }
        
        // Matching Analysis
        List<ApplicationScore> scores = applicationScoreRepository.findByApplicationId(id);
        
        if (scores.isEmpty()) {
            // Fallback: Calculate on the fly for legacy applications missing breakdown in DB
            int s = calculateSkillsMatch(student, internship);
            int g = calculateGpaMatch(student, internship);
            int e = calculateExpMatch(student, internship);
            int c = calculateCertMatch(student, internship);
            
            scores = Arrays.asList(
                new ApplicationScore(id, "Skills", internship.getSkillsWeight()!=null?internship.getSkillsWeight():40, internship.getRequiredSkills(), student.getSkills(), s),
                new ApplicationScore(id, "GPA", internship.getGpaWeight()!=null?internship.getGpaWeight():30, "Min "+internship.getMinGpa(), student.getGpa()!=null?student.getGpa().toString():"0.0", g),
                new ApplicationScore(id, "Experience", internship.getExperienceWeight()!=null?internship.getExperienceWeight():20, "Relevant Projects", (student.getExperience()!=null && !student.getExperience().isEmpty())?"Provided":"Not Provided", e),
                new ApplicationScore(id, "Certificates", internship.getCertificatesWeight()!=null?internship.getCertificatesWeight():10, "Recognized Certs", (student.getCertifications()!=null && !student.getCertifications().isEmpty())?"Provided":"Not Provided", c)
            );
        }
        
        model.addAttribute("app", app);
        model.addAttribute("internship", internship);
        model.addAttribute("student", student);
        model.addAttribute("scores", scores);
        
        int totalMatch = scores.stream().mapToInt(ApplicationScore::getScoreValue).sum();
        model.addAttribute("totalMatch", totalMatch);

        if (internship.getCompanyId() != null) {
            Company company = companyService.findById(internship.getCompanyId());
            model.addAttribute("company", company);
        }

        // Timeline (UI phase, but includes the real application date)
        model.addAttribute("timeline", List.of(
                Map.of("step", "Applied", "date", app.getAppliedAt() != null ? app.getAppliedAt().toString().substring(0, 10) : "Recent", "done", true),
                Map.of("step", "Processing", "date", "In Progress", "done", false),
                Map.of("step", "Shortlisted", "date", "—", "done", false)
        ));

        return "student/application-detail";
    }

    private int calculateSkillsMatch(Student student, Internship internship) {
        Integer weightObj = internship.getSkillsWeight();
        int weight = (weightObj != null) ? weightObj.intValue() : 40;
        
        if (internship.getRequiredSkills() == null || internship.getRequiredSkills().isEmpty()) return weight;
        if (student.getSkills() == null || student.getSkills().isEmpty()) return 0;

        Set<String> required = Arrays.stream(internship.getRequiredSkills().toLowerCase().split("[,\\s]+"))
                .filter(s -> !s.isBlank()).collect(Collectors.toSet());
        Set<String> studentSkills = Arrays.stream(student.getSkills().toLowerCase().split("[,\\s]+"))
                .filter(s -> !s.isBlank()).collect(Collectors.toSet());

        long matches = required.stream().filter(studentSkills::contains).count();
        double ratio = (double) matches / required.size();
        
        return (int) (ratio * weight);
    }

    private int calculateGpaMatch(Student student, Internship internship) {
        Integer weightObj = internship.getGpaWeight();
        int weight = (weightObj != null) ? weightObj.intValue() : 30;
        
        if (internship.getMinGpa() == null || internship.getMinGpa() == 0) return weight;
        if (student.getGpa() == null) return 0;

        if (student.getGpa() >= internship.getMinGpa()) return weight;
        
        double ratio = student.getGpa() / internship.getMinGpa();
        return (int) (ratio * weight);
    }

    private int calculateExpMatch(Student student, Internship internship) {
        Integer weightObj = internship.getExperienceWeight();
        int weight = (weightObj != null) ? weightObj.intValue() : 20;
        
        if (student.getExperience() == null || student.getExperience().isEmpty()) return 0;
        
        // Basic match based on presence of experience text (for UI phase)
        if (student.getExperience().length() > 50) return weight;
        return (int) ((student.getExperience().length() / 50.0) * weight);
    }

    private int calculateCertMatch(Student student, Internship internship) {
        Integer weightObj = internship.getCertificatesWeight();
        int weight = (weightObj != null) ? weightObj.intValue() : 10;
        
        if (student.getCertifications() == null || student.getCertifications().isEmpty()) return 0;
        
        if (student.getCertifications().length() > 20) return weight;
        return (int) ((student.getCertifications().length() / 20.0) * weight);
    }
}