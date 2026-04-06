package com.uniintern.portal.admin;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLoggingAspect {

    private final AuditLogRepository auditLogRepository;

    public AuditLoggingAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @AfterReturning("execution(* com.uniintern.portal.admin.AdminController.approveCompany(Long)) && args(id)")
    public void logCompanyApproval(JoinPoint joinPoint, Long id) {
        saveLog("Approve", "Company", "ID: " + id);
    }

    @AfterReturning("execution(* com.uniintern.portal.admin.AdminController.rejectCompany(Long)) && args(id)")
    public void logCompanyRejection(JoinPoint joinPoint, Long id) {
        saveLog("Reject", "Company", "ID: " + id);
    }

    @AfterReturning("execution(* com.uniintern.portal.admin.AdminController.approveInternship(Long)) && args(id)")
    public void logInternshipApproval(JoinPoint joinPoint, Long id) {
        saveLog("Approve", "Internship", "ID: " + id);
    }

    @AfterReturning("execution(* com.uniintern.portal.admin.AdminController.rejectInternship(Long)) && args(id)")
    public void logInternshipRejection(JoinPoint joinPoint, Long id) {
        saveLog("Reject", "Internship", "ID: " + id);
    }

    @AfterReturning(pointcut = "execution(* com.uniintern.portal.admin.AdminController.createInterview(..))", returning = "result")
    public void logInterviewCreation(JoinPoint joinPoint, Object result) {
        // If it successfully redirects, we log it
        if (result != null && result.toString().startsWith("redirect:")) {
            saveLog("Schedule", "Interview", "New Interview Scheduled");
        }
    }

    private void saveLog(String action, String targetType, String targetName) {
        AuditLog log = new AuditLog(action, targetType, targetName, "System Admin");
        auditLogRepository.save(log);
    }
}
