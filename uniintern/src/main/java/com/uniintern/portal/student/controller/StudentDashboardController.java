package com.uniintern.portal.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
public class StudentDashboardController {

    @Autowired
    private com.uniintern.portal.student.repository.StudentRepository studentRepository;

    @Autowired
    private com.uniintern.portal.student.repository.StudentApplicationRepository studentApplicationRepository;

    @Autowired
    private com.uniintern.portal.student.service.NotificationService notificationService;

    @Autowired
    private com.uniintern.portal.company.service.InternshipService internshipService;

    @Autowired
    private com.uniintern.portal.company.repository.InterviewRepository interviewRepository;

    @GetMapping("/student/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) return "redirect:/student/login";

        com.uniintern.portal.student.model.Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return "redirect:/student/login";

        // 1. Profile Completion
        int completion = calculateProfileCompletion(student);
        model.addAttribute("profileCompletion", completion);
        model.addAttribute("fullName", student.getFullName());

        // 2. Counts
        long appCount = studentApplicationRepository.findByStudentId(studentId).size();
        long interviewCount = interviewRepository.findByStudentId(studentId).size();
        long unreadNotifs = notificationService.getUnreadCount(studentId);

        model.addAttribute("activeApplications", appCount);
        model.addAttribute("interviews", interviewCount);
        model.addAttribute("notificationCount", unreadNotifs);

        // 3. Latest Notifications (Top 5)
        List<com.uniintern.portal.student.model.Notification> realNotifs = notificationService.getNotificationsForStudent(studentId);
        List<Map<String, Object>> displayNotifs = realNotifs.stream()
            .limit(5)
            .map(n -> {
                Map<String, Object> map = new HashMap<>();
                map.put("text", n.getMessage());
                map.put("date", n.getCreatedAt().toString().substring(0, 10));
                return map;
            }).collect(Collectors.toList());
        model.addAttribute("latestNotifications", displayNotifs);

        // 4. Latest Internships (Top 4)
        List<com.uniintern.portal.company.dto.InternshipListingDto> allInternships = internshipService.getApprovedInternshipsListings();
        List<Map<String, Object>> recentInternships = allInternships.stream()
            .limit(4)
            .map(i -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", i.getId());
                map.put("title", i.getTitle());
                map.put("company", i.getCompanyName());
                map.put("location", i.getLocation());
                return map;
            }).collect(Collectors.toList());
        model.addAttribute("recentInternships", recentInternships);

        return "student/dashboard";
    }

    private int calculateProfileCompletion(com.uniintern.portal.student.model.Student s) {
        int fieldsFilled = 0;
        int totalFields = 12;

        if (s.getFullName() != null && !s.getFullName().isEmpty()) fieldsFilled++;
        if (s.getEmail() != null && !s.getEmail().isEmpty()) fieldsFilled++;
        if (s.getUniversity() != null && !s.getUniversity().isEmpty()) fieldsFilled++;
        if (s.getRegistrationNumber() != null && !s.getRegistrationNumber().isEmpty()) fieldsFilled++;
        if (s.getNicNumber() != null && !s.getNicNumber().isEmpty()) fieldsFilled++;
        if (s.getDegreeProgram() != null && !s.getDegreeProgram().isEmpty()) fieldsFilled++;
        if (s.getAcademicYear() != null && !s.getAcademicYear().isEmpty()) fieldsFilled++;
        if (s.getGpa() != null && s.getGpa() > 0) fieldsFilled++;
        if (s.getSkills() != null && !s.getSkills().isEmpty()) fieldsFilled++;
        if (s.getExperience() != null && !s.getExperience().isEmpty()) fieldsFilled++;
        if (s.getCertifications() != null && !s.getCertifications().isEmpty()) fieldsFilled++;
        if (s.getCvFilePath() != null && !s.getCvFilePath().isEmpty()) fieldsFilled++;

        return (int) ((fieldsFilled / (float) totalFields) * 100);
    }

    @GetMapping("/student/settings")
    public String settings(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) return "redirect:/student/login";

        com.uniintern.portal.student.model.Student student = studentRepository.findById(studentId).orElse(null);
        model.addAttribute("student", student);
        return "student/settings";
    }

    @PostMapping("/student/settings/update-email")
    public String updateEmail(HttpSession session, @org.springframework.web.bind.annotation.RequestParam String email, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId != null) {
            com.uniintern.portal.student.model.Student student = studentRepository.findById(studentId).orElse(null);
            if (student != null) {
                student.setEmail(email);
                studentRepository.save(student);
                ra.addFlashAttribute("success", "Email updated successfully");
            }
        }
        return "redirect:/student/settings";
    }

    @PostMapping("/student/settings/change-password")
    public String changePassword(HttpSession session, 
                                @org.springframework.web.bind.annotation.RequestParam String currentPassword,
                                @org.springframework.web.bind.annotation.RequestParam String newPassword,
                                org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId != null) {
            com.uniintern.portal.student.model.Student student = studentRepository.findById(studentId).orElse(null);
            if (student != null) {
                if (student.getPassword().equals(currentPassword)) {
                    student.setPassword(newPassword);
                    studentRepository.save(student);
                    ra.addFlashAttribute("success", "Password changed successfully");
                } else {
                    ra.addFlashAttribute("error", "Current password incorrect");
                }
            }
        }
        return "redirect:/student/settings";
    }

    @PostMapping("/student/settings/update-preferences")
    public String updatePreferences(HttpSession session,
                                    @org.springframework.web.bind.annotation.RequestParam(required = false) boolean twoFactorEnabled,
                                    @org.springframework.web.bind.annotation.RequestParam(required = false) boolean loginAlertsEnabled,
                                    @org.springframework.web.bind.annotation.RequestParam(required = false) boolean rememberDeviceEnabled,
                                    org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId != null) {
            com.uniintern.portal.student.model.Student student = studentRepository.findById(studentId).orElse(null);
            if (student != null) {
                student.setTwoFactorEnabled(twoFactorEnabled);
                student.setLoginAlertsEnabled(loginAlertsEnabled);
                student.setRememberDeviceEnabled(rememberDeviceEnabled);
                studentRepository.save(student);
                ra.addFlashAttribute("success", "Preferences updated successfully");
            }
        }
        return "redirect:/student/settings";
    }
}