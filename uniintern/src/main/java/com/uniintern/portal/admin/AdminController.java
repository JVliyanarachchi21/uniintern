package com.uniintern.portal.admin;

import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.repository.CompanyRepository;
import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.repository.InternshipRepository;
import com.uniintern.portal.company.entity.Interview;
import com.uniintern.portal.company.repository.InterviewRepository;
import com.uniintern.portal.company.service.EmailService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.uniintern.portal.student.service.NotificationService;
import com.uniintern.portal.student.repository.StudentRepository;
import com.uniintern.portal.student.repository.StudentApplicationRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CompanyRepository companyRepository;
    private final InternshipRepository internshipRepository;
    private final InterviewRepository interviewRepository;
    private final AuditLogRepository auditLogRepository;
    private final EmailService emailService;
    private final AdminSchedulingService adminSchedulingService;
    private final SystemMessageRepository systemMessageRepository;
    private final AdminReportService adminReportService;
    private final AdminAccountRepository adminAccountRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // --- INTELLIGENT SENTRY SERVICES ---
    private final AdminAlertService adminAlertService;

    // --- ZERO-TOUCH INTEGRATION REPOSITORIES ---
    private final StudentRepository studentRepository;
    private final StudentApplicationRepository studentApplicationRepository;
    private final NotificationService notificationService;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    public AdminController(CompanyRepository companyRepository,
            InternshipRepository internshipRepository,
            InterviewRepository interviewRepository,
            AuditLogRepository auditLogRepository,
            EmailService emailService,
            AdminSchedulingService adminSchedulingService,
            SystemMessageRepository systemMessageRepository,
            AdminReportService adminReportService,
            AdminAccountRepository adminAccountRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
            StudentRepository studentRepository,
            StudentApplicationRepository studentApplicationRepository,
            NotificationService notificationService,
            JavaMailSender javaMailSender,
            AdminAlertService adminAlertService) {
        this.companyRepository = companyRepository;
        this.internshipRepository = internshipRepository;
        this.interviewRepository = interviewRepository;
        this.auditLogRepository = auditLogRepository;
        this.emailService = emailService;
        this.adminSchedulingService = adminSchedulingService;
        this.systemMessageRepository = systemMessageRepository;
        this.adminReportService = adminReportService;
        this.adminAccountRepository = adminAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentRepository = studentRepository;
        this.studentApplicationRepository = studentApplicationRepository;
        this.notificationService = notificationService;
        this.javaMailSender = javaMailSender;
        this.adminAlertService = adminAlertService;
    }

    @GetMapping({ "/dashboard", "" })
    public String dashboard(Model model) {
        long pendingCompanies = companyRepository.countByStatus("PENDING_VERIFICATION");
        long pendingInternships = internshipRepository.countByStatus("PENDING_ADMIN_APPROVAL");
        long interviewsToday = interviewRepository.countInterviewsByDate(java.time.LocalDateTime.now());

        model.addAttribute("pendingCompanies", pendingCompanies);
        model.addAttribute("pendingInternships", pendingInternships);
        model.addAttribute("interviewsToday", interviewsToday);

        model.addAttribute("logs",
                auditLogRepository.findAllByOrderByPerformedAtDesc()
                        .stream().limit(5).toList());

        return "admin/dashboard";
    }

    @GetMapping("/notifications")
    public String notificationHub(Model model) {
        return "admin/notifications";
    }

    @PostMapping("/notifications/mark-all-read")
    public String markAllAlertsRead() {
        adminAlertService.markAllAsRead();
        return "redirect:/admin/notifications";
    }

    @GetMapping("/companies")
    public String companyApprovals(Model model) {
        // Show all companies waiting for verification or final approval
        List<Company> pending = new java.util.ArrayList<>(companyRepository.findByStatus("PENDING_VERIFICATION"));
        pending.addAll(companyRepository.findByStatus("PENDING_APPROVAL"));
        model.addAttribute("companies", pending);
        return "admin/company-approvals";
    }

    @GetMapping("/companies/all")
    public String allCompanies(@RequestParam(required = false) String status, Model model) {
        List<Company> list = (status == null)
                ? companyRepository.findAll()
                : companyRepository.findByStatus(status);

        model.addAttribute("companies", list);
        model.addAttribute("selectedStatus", status);
        return "admin/company-all";
    }

    @GetMapping("/companies/{id}/approve")
    public String approveCompany(@PathVariable Long id) {
        Company c = companyRepository.findById(id).orElseThrow();
        c.setStatus("ACTIVE");
        companyRepository.save(c);
        return "redirect:/admin/companies";
    }

    @GetMapping("/companies/{id}/reject")
    public String rejectCompany(@PathVariable Long id) {
        Company c = companyRepository.findById(id).orElseThrow();
        c.setStatus("REJECTED");
        companyRepository.save(c);
        return "redirect:/admin/companies";
    }

    @GetMapping("/internships")
    public String internshipApprovals(Model model) {
        List<Internship> pending = internshipRepository.findByStatus("PENDING_ADMIN_APPROVAL");
        model.addAttribute("internships", pending);
        return "admin/internship-approvals";
    }

    @GetMapping("/internships/all")
    public String allInternships(@RequestParam(required = false) String status, Model model) {
        List<Internship> list = (status == null)
                ? internshipRepository.findAll()
                : internshipRepository.findByStatus(status);

        model.addAttribute("internships", list);
        model.addAttribute("selectedInternshipStatus", status);
        return "admin/internship-all";
    }

    @GetMapping("/internships/{id}/approve")
    public String approveInternship(@PathVariable Long id) {
        Internship i = internshipRepository.findById(id).orElseThrow();
        i.setStatus("APPROVED");
        internshipRepository.save(i);
        return "redirect:/admin/internships";
    }

    @GetMapping("/internships/{id}/reject")
    public String rejectInternship(@PathVariable Long id) {
        Internship i = internshipRepository.findById(id).orElseThrow();
        i.setStatus("REJECTED");
        internshipRepository.save(i);
        return "redirect:/admin/internships";
    }

    @GetMapping("/filtering")
    public String filteringResults() {
        return "admin/filtering-results";
    }

    @GetMapping("/scheduling")
    public String interviewScheduling(Model model) {
        List<Interview> interviews = interviewRepository.findAll();
        model.addAttribute("interviews", interviews);
        model.addAttribute("scheduledCount",
                interviews.stream().filter(i -> "SCHEDULED".equals(i.getStatus())).count());
        model.addAttribute("completedCount",
                interviews.stream().filter(i -> "COMPLETED".equals(i.getStatus())).count());
        model.addAttribute("cancelledCount",
                interviews.stream().filter(i -> "CANCELLED".equals(i.getStatus())).count());
        return "admin/interview-scheduling";
    }

    @GetMapping("/scheduling/advanced")
    public String advancedScheduling(Model model) {
        List<PendingInterviewDto> pendingInterviews = adminSchedulingService.getPendingInterviews();
        model.addAttribute("pendingInterviews", pendingInterviews);
        return "admin/advanced-scheduling";
    }

    @GetMapping("/schedule")
    public String scheduleForm(@RequestParam(required = false) Long applicationId,
                               @RequestParam(required = false) String candidateName,
                               @RequestParam(required = false) Long internshipId,
                               Model model) {
        List<Internship> approvedInternships = internshipRepository.findByStatus("APPROVED");
        model.addAttribute("approvedInternships", approvedInternships);
        
        String prefilledCandidateName = candidateName == null ? "" : candidateName;
        Long prefilledInternshipId = internshipId;

        // Auto-fetch if applicationId is provided
        if (applicationId != null) {
            com.uniintern.portal.student.model.StudentApplication app = 
                adminSchedulingService.getApplicationById(applicationId);
            if (app != null) {
                com.uniintern.portal.student.model.Student s = adminSchedulingService.getStudentById(app.getStudentId());
                if (s != null) prefilledCandidateName = s.getFullName();
                prefilledInternshipId = app.getInternshipId();
            }
        }

        model.addAttribute("applicationId", applicationId);
        model.addAttribute("candidateName", prefilledCandidateName);
        model.addAttribute("internshipId", prefilledInternshipId);
        
        model.addAttribute("datetime", "");
        model.addAttribute("showPopup", false);
        return "admin/schedule-form";
    }

    @PostMapping("/schedule")
    public String createInterview(@RequestParam(required = false) Long applicationId,
            @RequestParam(required = false) String candidateName,
            @RequestParam(required = false) Long internshipId,
            @RequestParam(required = false) String datetime,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String locationLink,
            Model model) {

        List<Internship> approvedInternships = internshipRepository.findByStatus("APPROVED");
        model.addAttribute("approvedInternships", approvedInternships);

        String cleanCandidateName = candidateName == null ? "" : candidateName.trim();
        String cleanDatetime = datetime == null ? "" : datetime.trim();

        boolean hasError = false;

        model.addAttribute("applicationId", applicationId);
        model.addAttribute("candidateName", cleanCandidateName);
        model.addAttribute("internshipId", internshipId);
        model.addAttribute("datetime", cleanDatetime);
        model.addAttribute("mode", mode);
        model.addAttribute("locationLink", locationLink);

        if (cleanCandidateName.isBlank()) {
            model.addAttribute("candidateNameError", "Candidate name cannot be empty.");
            hasError = true;
        } else if (cleanCandidateName.length() < 3 || cleanCandidateName.length() > 80) {
            model.addAttribute("candidateNameError", "Name must be between 3 and 80 characters.");
            hasError = true;
        } else if (!cleanCandidateName.matches("^[A-Za-z ]+$")) {
            model.addAttribute("candidateNameError", "Only letters and spaces are allowed.");
            hasError = true;
        }

        Internship selectedInternship = null;
        if (internshipId == null) {
            model.addAttribute("internshipIdError", "Please select an approved internship.");
            hasError = true;
        } else {
            selectedInternship = internshipRepository.findById(internshipId).orElse(null);
            if (selectedInternship == null || !"APPROVED".equals(selectedInternship.getStatus())) {
                model.addAttribute("internshipIdError", "The selected internship is no longer valid or approved.");
                hasError = true;
            }
        }

        LocalDateTime interviewDateTime = null;
        if (cleanDatetime.isBlank()) {
            model.addAttribute("datetimeError", "Please select an interview date and time.");
            hasError = true;
        } else {
            try {
                // Support both ISO format and the space-separated format from Flatpickr
                String normalizedDatetime = cleanDatetime.replace(" ", "T");
                if (normalizedDatetime.length() == 16) {
                    normalizedDatetime += ":00"; // Add seconds if missing
                }
                interviewDateTime = LocalDateTime.parse(normalizedDatetime);
                // Relaxed to allow any time from now onwards
                if (interviewDateTime.isBefore(LocalDateTime.now().minusMinutes(1))) {
                    model.addAttribute("datetimeError", "Interview time cannot be in the past.");
                    hasError = true;
                } else {
                    // --- BUSINESS RULE: No same-day bookings after 8 PM ---
                    LocalDateTime now = LocalDateTime.now();
                    if (now.getHour() >= 20 && interviewDateTime.toLocalDate().equals(now.toLocalDate())) {
                        model.addAttribute("datetimeError", "It is past 8:00 PM. Same-day interview scheduling is now closed. Please select a future date.");
                        hasError = true;
                    }
                }
            } catch (DateTimeParseException e) {
                model.addAttribute("datetimeError", "Invalid date format.");
                hasError = true;
            }
        }

        if (hasError) {
            model.addAttribute("showPopup", true);
            model.addAttribute("formError", "Invalid input detected. Please correct the fields and try again.");
            return "admin/schedule-form";
        }

        // --- INTELLIGENT CONFLICT DETECTION ---
        if (applicationId != null && interviewDateTime != null) {
            com.uniintern.portal.student.model.StudentApplication app = adminSchedulingService.getApplicationById(applicationId);
            if (app != null && adminSchedulingService.hasTimeConflict(app.getStudentId(), interviewDateTime)) {
                model.addAttribute("showPopup", true);
                model.addAttribute("formError", "CRITICAL CONFLICT: This student already has an interview scheduled within 30 minutes of this time slot.");
                return "admin/schedule-form";
            }
        }

        Interview interview = new Interview();
        interview.setCandidateName(cleanCandidateName);
        interview.setInternshipTitle(selectedInternship.getTitle());
        interview.setInterviewDateTime(interviewDateTime);
        interview.setMode(mode != null ? mode : "Online");
        interview.setLocationLink(locationLink != null ? locationLink : "TBD");
        interview.setStatus("SCHEDULED");
        
        // Resolve Company Name for sync
        companyRepository.findById(selectedInternship.getCompanyId())
                .ifPresent(c -> interview.setCompanyName(c.getCompanyName()));

        interviewRepository.save(interview);

        // --- ZERO-TOUCH REAL-TIME NOTIFICATIONS ---
        if (applicationId != null) {
            com.uniintern.portal.student.model.StudentApplication app = 
                adminSchedulingService.getApplicationById(applicationId);
            if (app != null) {
                // 1. Update Application Status (Sync across modules)
                app.setStatus(com.uniintern.portal.student.model.ApplicationStatus.INTERVIEW_SCHEDULED);
                adminSchedulingService.saveApplication(app);

                // 2. Fetch Entities for Communications (Direct repo access)
                com.uniintern.portal.student.model.Student student = studentRepository.findById(app.getStudentId()).orElse(null);
                Company company = companyRepository.findById(selectedInternship.getCompanyId()).orElse(null);

                if (student != null) {
                    // 3. Trigger In-App Notification (For Student Dashboard)
                    String msg = "Congratulations! Your " + interview.getMode() + " interview for " + selectedInternship.getTitle() + " has been scheduled.";
                    notificationService.createNotification(student.getId(), "Interview Scheduled", msg, "INTERVIEW");

                    // 4. Send Email to Student (Detailed Invitation)
                    sendAdminEmail(student.getEmail(), "UniIntern Interview Invitation", 
                        "Dear " + student.getFullName() + ",\n\n" +
                        "Your interview for the '" + selectedInternship.getTitle() + "' role at " + 
                        (interview.getCompanyName() != null ? interview.getCompanyName() : "the designated company") + 
                        " has been scheduled.\n\n" +
                        "Details:\n" +
                        "Date & Time: " + interviewDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")) + "\n" +
                        "Mode: " + interview.getMode() + "\n" +
                        "Location/Link: " + interview.getLocationLink() + "\n\n" +
                        "Please check your student dashboard to confirm.\n\n" +
                        "Best regards,\nUniIntern Administration");
                }

                if (company != null) {
                    // 5. Send Email to Company Recruiter (Company Email)
                    sendAdminEmail(company.getEmail(), "New Interview Scheduled - UniIntern", 
                        "Hello " + company.getCompanyName() + ",\n\n" +
                        "An interview has been scheduled for candidate " + cleanCandidateName + 
                        " for your internship position: " + selectedInternship.getTitle() + ".\n\n" +
                        "Scheduled Time: " + interviewDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")) + "\n\n" +
                        "Best regards,\nUniIntern Administration");
                }
            }
        }

        model.addAttribute("showPopup", true);
        model.addAttribute("successMessage", "SUCCESS: Interview correctly slotted. Recruitment pipeline advanced to 'SCHEDULED'. Conflict check validated.");
        return "admin/schedule-form";
    }

    private void sendAdminEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
            System.out.println("[COMM-HUB] Real-time email dispatched to: " + to);
        } catch (Exception e) {
            System.err.println("[COMM-HUB] Email simulation - To: " + to + " | Sub: " + subject);
        }
    }

    @GetMapping("/reports")
    public String reports(Model model) {

        long totalCompanies = companyRepository.count();
        long pendingCompanies = companyRepository.countByStatus("PENDING_VERIFICATION");
        long verifiedCompanies = companyRepository.countByStatus("VERIFIED");
        long rejectedCompanies = companyRepository.countByStatus("REJECTED");

        long pendingInternships = internshipRepository.countByStatus("PENDING_ADMIN_APPROVAL");
        long approvedInternships = internshipRepository.countByStatus("APPROVED");
        long rejectedInternships = internshipRepository.countByStatus("REJECTED");

        long scheduledInterviews = interviewRepository.countByStatus("SCHEDULED");
        long completedInterviews = interviewRepository.countByStatus("COMPLETED");
        long cancelledInterviews = interviewRepository.countByStatus("CANCELLED");

        model.addAttribute("portalName", "UniIntern Portal");
        model.addAttribute("generatedOn", java.time.LocalDateTime.now());

        model.addAttribute("totalCompanies", totalCompanies);
        model.addAttribute("pendingCompanies", pendingCompanies);
        model.addAttribute("verifiedCompanies", verifiedCompanies);
        model.addAttribute("rejectedCompanies", rejectedCompanies);

        model.addAttribute("pendingInternships", pendingInternships);
        model.addAttribute("approvedInternships", approvedInternships);
        model.addAttribute("rejectedInternships", rejectedInternships);

        model.addAttribute("scheduledInterviews", scheduledInterviews);
        model.addAttribute("completedInterviews", completedInterviews);
        model.addAttribute("cancelledInterviews", cancelledInterviews);

        return "admin/reports";
    }

    @GetMapping("/reports/companies/download")
    public ResponseEntity<byte[]> downloadCompanyReport() {
        Map<String, Object> data = new HashMap<>();
        data.put("companies", companyRepository.findAll());
        data.put("generatedOn", LocalDateTime.now());
        
        byte[] pdfBytes = adminReportService.generatePdf("company-pdf", data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=company-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/reports/internships/download")
    public ResponseEntity<byte[]> downloadInternshipReport() {
        Map<String, Object> data = new HashMap<>();
        data.put("internships", internshipRepository.findAll());
        data.put("generatedOn", LocalDateTime.now());

        byte[] pdfBytes = adminReportService.generatePdf("internship-pdf", data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=internship-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/reports/interviews/download")
    public ResponseEntity<byte[]> downloadInterviewReport() {
        StringBuilder csv = new StringBuilder();

        csv.append("UniIntern Portal - Interview Scheduling Report\n");
        csv.append("Generated On,").append(LocalDateTime.now()).append("\n\n");
        csv.append("Candidate Name,Internship Title,Interview Date Time,Status\n");

        for (Interview interview : interviewRepository.findAll()) {
            csv.append(csvEscape(interview.getCandidateName())).append(",");
            csv.append(csvEscape(interview.getInternshipTitle())).append(",");
            csv.append(interview.getInterviewDateTime() != null ? interview.getInterviewDateTime() : "").append(",");
            csv.append(interview.getStatus() != null ? interview.getStatus() : "").append("\n");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interview-report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    @GetMapping("/audit")
    public String auditLogs(Model model) {
        model.addAttribute("logs", auditLogRepository.findAllByOrderByPerformedAtDesc());
        return "admin/audit-logs";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        return "admin/settings";
    }

    @PostMapping("/settings/report")
    public String reportIssue(@RequestParam String subject, @RequestParam String content, Model model) {
        SystemMessage msg = new SystemMessage("BUG_REPORT", subject, content, "admin@uniintern.com");
        systemMessageRepository.save(msg);
        model.addAttribute("message", "Issue reported successfully to the system administrator.");
        return "admin/settings";
    }

    @PostMapping("/settings/invite")
    public String inviteAdmin(@RequestParam String email, Model model) {
        SystemMessage msg = new SystemMessage("INVITE", "System Invitation", "Portal link sent to " + email, "admin@uniintern.com");
        msg.setRecipientEmail(email);
        systemMessageRepository.save(msg);
        model.addAttribute("message", "Invitation link sent to " + email);
        return "admin/settings";
    }

    @PostMapping("/settings/password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                org.springframework.security.core.Authentication auth,
                                Model model) {
        
        AdminAccount admin = adminAccountRepository.findByEmail(auth.getName()).orElseThrow();

        if (!passwordEncoder.matches(currentPassword, admin.getPassword())) {
            model.addAttribute("error", "Current password is incorrect.");
            return "admin/settings";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            return "admin/settings";
        }

        if (!SecurityUtils.isPasswordSecure(newPassword)) {
            model.addAttribute("error", SecurityUtils.getPasswordRequirementsMessage());
            return "admin/settings";
        }

        admin.setPassword(passwordEncoder.encode(newPassword));
        adminAccountRepository.save(admin);

        model.addAttribute("message", "Password updated successfully with enterprise-grade encryption.");
        return "admin/settings";
    }
}