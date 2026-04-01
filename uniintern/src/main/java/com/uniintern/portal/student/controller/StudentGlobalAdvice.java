package com.uniintern.portal.student.controller;

import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.uniintern.portal.student.controller")
public class StudentGlobalAdvice {

    @Autowired
    private StudentRepository studentRepository;

    @ModelAttribute("loggedInStudent")
    public Student addLoggedInStudent(HttpSession session) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId != null) {
            return studentRepository.findById(studentId).orElse(null);
        }
        return null;
    }
    
    @ModelAttribute("fullName")
    public String addFullName(HttpSession session) {
        Student student = addLoggedInStudent(session);
        return student != null ? student.getFullName() : "Guest User";
    }
}
