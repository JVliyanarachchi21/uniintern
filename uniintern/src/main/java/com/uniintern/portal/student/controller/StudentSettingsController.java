package com.uniintern.portal.student.controller;

import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentSettingsController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/student/settings")
    public String settings(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) return "redirect:/student/login";

        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return "redirect:/student/login";

        model.addAttribute("student", student);
        model.addAttribute("fullName", student.getFullName());
        model.addAttribute("activePage", "settings");
        return "student/settings";
    }

    @PostMapping("/student/settings/update-email")
    public String updateEmail(HttpSession session, @RequestParam String email, RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId != null) {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student != null) {
                student.setEmail(email);
                studentRepository.save(student);
                ra.addFlashAttribute("success", "Security Note: Contact email updated successfully.");
            }
        }
        return "redirect:/student/settings";
    }

    @PostMapping("/student/settings/change-password")
    public String changePassword(HttpSession session, 
                                @RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId != null) {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student != null) {
                // 1. Check current password
                if (!passwordEncoder.matches(currentPassword, student.getPassword())) {
                    ra.addFlashAttribute("error", "Current password incorrect");
                    return "redirect:/student/settings";
                }

                // 2. Check match
                if (!newPassword.equals(confirmPassword)) {
                    ra.addFlashAttribute("error", "New passwords do not match");
                    return "redirect:/student/settings";
                }

                // 3. Update with encoding
                student.setPassword(passwordEncoder.encode(newPassword));
                studentRepository.save(student);
                ra.addFlashAttribute("success", "Password updated with enterprise-grade encryption.");
            }
        }
        return "redirect:/student/settings";
    }

    @PostMapping("/student/settings/update-preferences")
    public String updatePreferences(HttpSession session,
                                    @RequestParam(required = false) boolean twoFactorEnabled,
                                    @RequestParam(required = false) boolean loginAlertsEnabled,
                                    @RequestParam(required = false) boolean rememberDeviceEnabled,
                                    RedirectAttributes ra) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId != null) {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student != null) {
                student.setTwoFactorEnabled(twoFactorEnabled);
                student.setLoginAlertsEnabled(loginAlertsEnabled);
                student.setRememberDeviceEnabled(rememberDeviceEnabled);
                studentRepository.save(student);
                ra.addFlashAttribute("success", "Security preferences updated.");
            }
        }
        return "redirect:/student/settings";
    }
}
