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
    private final com.uniintern.portal.student.repository.StudentRepository studentRepository;
    private final com.uniintern.portal.student.repository.StudentApplicationRepository studentApplicationRepository;
    private final AdminAccountRepository adminAccountRepository;

    public AdminAssistantService(CompanyRepository companyRepository,
                                 InternshipRepository internshipRepository,
                                 InterviewRepository interviewRepository,
                                 AuditLogRepository auditLogRepository,
                                 com.uniintern.portal.student.repository.StudentRepository studentRepository,
                                 com.uniintern.portal.student.repository.StudentApplicationRepository studentApplicationRepository,
                                 AdminAccountRepository adminAccountRepository) {
        this.companyRepository = companyRepository;
        this.internshipRepository = internshipRepository;
        this.interviewRepository = interviewRepository;
        this.auditLogRepository = auditLogRepository;
        this.studentRepository = studentRepository;
        this.studentApplicationRepository = studentApplicationRepository;
        this.adminAccountRepository = adminAccountRepository;
    }

    public String getResponseForQuery(String query) {
        String lowerQuery = query.toLowerCase();

        // 1. SYSTEM OVERVIEW (ALL DATA)
        if (lowerQuery.contains("system") || lowerQuery.contains("all") || lowerQuery.contains("overview") || lowerQuery.contains("platform")) {
            long students = studentRepository.count();
            long companies = companyRepository.count();
            long applications = studentApplicationRepository.count();
            long internships = internshipRepository.count();
            return String.format("SYSTEM INTELLIGENCE OVERVIEW: The UniIntern platform is currently managing %d students and %d companies. " +
                                 "There are %d active internship listings and %d total applications in the recruitment pipeline.", 
                                 students, companies, internships, applications);
        }

        // 2. STUDENT & CANDIDATE INTELLIGENCE
        if (lowerQuery.contains("student") || lowerQuery.contains("candidate") || lowerQuery.contains("applicant")) {
            long total = studentRepository.count();
            long verified = studentRepository.findAll().stream()
                    .filter(s -> s.getStatus() == com.uniintern.portal.student.model.StudentStatus.VERIFIED)
                    .count();
            return String.format("CANDIDATE METRICS: There are %d registered students in the database. " + 
                                 "Currently, %d students (%d%%) have completed their OTP verification and are active.", 
                                 total, verified, total > 0 ? (verified * 100 / total) : 0);
        }

        // 3. APPLICATION & RECRUITMENT VOLUME
        if (lowerQuery.contains("application") || lowerQuery.contains("apply") || lowerQuery.contains("submission")) {
            long total = studentApplicationRepository.count();
            long pending = studentApplicationRepository.findAll().stream()
                    .filter(a -> a.getStatus() == com.uniintern.portal.student.model.StudentApplication.class.cast(a).getStatus()) // Just count all for now as status logic varies
                    .count();
            return "RECRUITMENT VOLUME: I've tracked " + total + " total applications across the system. " +
                   "You can monitor specific candidate progress in the ‘Interview Scheduling’ hub.";
        }

        // 4. COMPANY & PARTNER INTELLIGENCE
        if (lowerQuery.contains("company") || lowerQuery.contains("companies") || lowerQuery.contains("partner")) {
            long count = companyRepository.count();
            long pending = companyRepository.findByStatus("PENDING_VERIFICATION").size();
            return "PARTNER METRICS: There are " + count + " companies in the ecosystem. " +
                   (pending > 0 ? "ATTENTION: " + pending + " companies are currently PENDING VERIFICATION and require your approval." 
                               : "All registered companies are currently verified.");
        }

        // 5. INTERNSHIP & OPPORTUNITY INTELLIGENCE
        if (lowerQuery.contains("internship") || lowerQuery.contains("job") || lowerQuery.contains("position")) {
            long total = internshipRepository.count();
            long pending = internshipRepository.findByStatus("PENDING_ADMIN_APPROVAL").size();
            return "OPPORTUNITY TRACKING: The system is hosting " + total + " internships. " +
                   (pending > 0 ? "There are " + pending + " new listings waiting for Admin approval." 
                               : "All internship listings have been processed.");
        }

        // 6. INTERVIEW & SCHEDULING
        if (lowerQuery.contains("interview") || lowerQuery.contains("schedule") || lowerQuery.contains("meeting")) {
            long total = interviewRepository.count();
            return "SCHEDULING ENGINE: " + total + " interview slots have been recorded. " +
                   "For real-time conflict detection and zero-touch scheduling, please use the Advanced Scheduling Hub.";
        }

        // 7. AUDIT & SECURITY
        if (lowerQuery.contains("log") || lowerQuery.contains("audit") || lowerQuery.contains("security") || lowerQuery.contains("admin")) {
            List<AuditLog> logs = auditLogRepository.findAllByOrderByPerformedAtDesc();
            long admins = adminAccountRepository.count();
            String response = "SECURITY & ACCESS: There are " + admins + " administrative accounts active. ";
            if (!logs.isEmpty()) {
                response += "The latest system event was: '" + logs.get(0).getAction() + "' on " + logs.get(0).getTargetType() + ".";
            } else {
                response += "No critical audit logs have been recorded in this session.";
            }
            return response;
        }
        
        if (lowerQuery.contains("hello") || lowerQuery.contains("hi") || lowerQuery.contains("help")) {
            return "Greetings, Admin. I am M.A.I.A, your central intelligence unit. I monitor all platform modules including Students, Companies, internships, and Security Logs. What system metrics do you require?";
        }

        return "I'm sorry, that query falls outside my current data-link parameters. Try asking about 'students', 'applications', 'companies', or 'system overview'.";
    }
}
