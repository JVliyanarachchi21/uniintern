package com.uniintern.portal.admin;

import com.uniintern.portal.company.repository.CompanyRepository;
import com.uniintern.portal.company.repository.InternshipRepository;
import com.uniintern.portal.company.repository.InterviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAssistantService {

    private final CompanyRepository companyRepository;
    private final InternshipRepository internshipRepository;
    private final InterviewRepository interviewRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminAssistantService(CompanyRepository companyRepository,
                                 InternshipRepository internshipRepository,
                                 InterviewRepository interviewRepository,
                                 AuditLogRepository auditLogRepository) {
        this.companyRepository = companyRepository;
        this.internshipRepository = internshipRepository;
        this.interviewRepository = interviewRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public String getResponseForQuery(String query) {
        String lowerQuery = query.toLowerCase();

        if (lowerQuery.contains("company") || lowerQuery.contains("companies")) {
            long count = companyRepository.count();
            long pending = companyRepository.findByStatus("PENDING_VERIFICATION").size();
            return "You currently have " + count + " total companies registered. There are " + pending + " companies waiting for your verification.";
        }

        if (lowerQuery.contains("internship")) {
            long pending = internshipRepository.findByStatus("PENDING_ADMIN_APPROVAL").size();
            return "There are " + pending + " internships pending approval right now.";
        }

        if (lowerQuery.contains("interview") || lowerQuery.contains("schedule")) {
            long total = interviewRepository.count();
            return "You have " + total + " total interview records in the system. Check the Advanced Hub for more pending applications.";
        }

        if (lowerQuery.contains("log") || lowerQuery.contains("audit")) {
            List<AuditLog> logs = auditLogRepository.findAllByOrderByPerformedAtDesc();
            if (logs.isEmpty()) {
                return "The audit logs are currently empty. But don't worry, the new AOP tracker is active and will log your next actions!";
            }
            return "Your last recorded action was: '" + logs.get(0).getAction() + " " + logs.get(0).getTargetType() + "'. You have a total of " + logs.size() + " logs recorded.";
        }
        
        if (lowerQuery.contains("hello") || lowerQuery.contains("hi")) {
            return "Hello! I am your AI Admin Assistant. I monitor your database in real-time. Try asking me about 'companies', 'internships', 'interviews', or 'logs'.";
        }

        return "I'm sorry, I'm just a specialized admin assistant. I didn't quite catch that. Try asking about companies, internships, interviews, or audit logs.";
    }
}
