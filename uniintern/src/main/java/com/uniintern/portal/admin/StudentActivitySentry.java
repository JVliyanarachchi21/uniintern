package com.uniintern.portal.admin;

import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.repository.StudentRepository;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
public class StudentActivitySentry {

    private final StudentRepository studentRepository;
    private final AdminAlertRepository adminAlertRepository;

    public StudentActivitySentry(StudentRepository studentRepository, 
                               AdminAlertRepository adminAlertRepository) {
        this.studentRepository = studentRepository;
        this.adminAlertRepository = adminAlertRepository;
    }

    @Pointcut("execution(* com.uniintern.portal.student.controller.StudentAuthController.handleLogin(..))")
    public void studentLogin() {}

    @AfterReturning(pointcut = "studentLogin() && args(email, ..)", argNames = "email")
    public void afterStudentLogin(String email) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setLastLoginAt(LocalDateTime.now());
            studentRepository.save(student);

            String msg = "LIVE FEED: Student [" + student.getFullName() + "] has just accessed the portal.";
            adminAlertRepository.save(new AdminAlert("SECURITY", "LOW", msg, "/admin/dashboard"));
            System.out.println("[SENTRY] Captured live login for: " + student.getEmail());
        }
    }

    @Pointcut("execution(* com.uniintern.portal.student.controller.StudentAuthController.handleRegister(..))")
    public void studentRegister() {}

    @AfterReturning(pointcut = "studentRegister() && args(name, email, ..)", argNames = "name,email")
    public void afterStudentRegister(String name, String email) {
        String msg = "SENTRY ALERT: New Candidate [" + name + "] has registered and joined the recruitment pool.";
        adminAlertRepository.save(new AdminAlert("REGISTRATION", "MEDIUM", msg, "/admin/reports"));
        System.out.println("[SENTRY] Captured new registration: " + email);
    }
}
