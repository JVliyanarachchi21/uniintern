package com.uniintern.portal.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Controller
public class StudentNotificationsController {

    @Autowired
    private com.uniintern.portal.student.service.NotificationService notificationService;

    @Autowired
    private com.uniintern.portal.student.repository.StudentRepository studentRepository;

    @GetMapping("/student/notifications")
    public String notifications(jakarta.servlet.http.HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) return "redirect:/student/login";

        com.uniintern.portal.student.model.Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            model.addAttribute("fullName", student.getFullName());
        }

        List<com.uniintern.portal.student.model.Notification> realNotifs = notificationService.getNotificationsForStudent(studentId);
        
        List<Map<String, Object>> displayNotifs = realNotifs.stream().map(n -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("type", n.getType());
            map.put("text", n.getMessage());
            map.put("date", n.getCreatedAt().toString().substring(0, 10)); // YYYY-MM-DD
            map.put("read", n.isRead());
            return map;
        }).collect(java.util.stream.Collectors.toList());

        model.addAttribute("notifications", displayNotifs);

        return "student/notifications";
    }

    @PostMapping("/student/notifications/mark-all-read")
    public String markAllRead(jakarta.servlet.http.HttpSession session) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId != null) {
            notificationService.markAllAsRead(studentId);
        }
        return "redirect:/student/notifications";
    }
}