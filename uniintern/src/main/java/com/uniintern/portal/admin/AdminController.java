package com.uniintern.portal.admin;

import com.uniintern.portal.company.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CompanyRepository companyRepository;
    private final InternshipRepository internshipRepository;
    private final InterviewRepository interviewRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminController(CompanyRepository companyRepository,
            InternshipRepository internshipRepository,
            InterviewRepository interviewRepository,
            AuditLogRepository auditLogRepository) {
        this.companyRepository = companyRepository;
        this.internshipRepository = internshipRepository;
        this.interviewRepository = interviewRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping({ "/dashboard", "" })
    public String dashboard(Model model) {

        long pendingCompanies = companyRepository
                .findByStatus(CompanyStatus.PENDING_VERIFICATION)
                .size();

        long pendingInternships = internshipRepository
                .findByStatus(InternshipStatus.PENDING_ADMIN_APPROVAL)
                .size();

        long interviewsToday = interviewRepository.findAll().stream()
                .filter(i -> i.getInterviewDateTime() != null)
                .filter(i -> i.getInterviewDateTime().toLocalDate()
                        .equals(java.time.LocalDate.now()))
                .count();

        model.addAttribute("pendingCompanies", pendingCompanies);
        model.addAttribute("pendingInternships", pendingInternships);
        model.addAttribute("interviewsToday", interviewsToday);

        model.addAttribute("logs",
                auditLogRepository.findAllByOrderByPerformedAtDesc()
                        .stream().limit(5).toList());

        return "admin/dashboard";
    }

    @GetMapping("/companies")
    public String companyApprovals(Model model) {
        List<Company> pending = companyRepository.findByStatus(CompanyStatus.PENDING_VERIFICATION);
        model.addAttribute("companies", pending);
        return "admin/company-approvals";
    }

    @GetMapping("/companies/all")
    public String allCompanies(@RequestParam(required = false) CompanyStatus status, Model model) {
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
        c.setStatus(CompanyStatus.VERIFIED);
        companyRepository.save(c);
        return "redirect:/admin/companies";
    }

    @GetMapping("/companies/{id}/reject")
    public String rejectCompany(@PathVariable Long id) {
        Company c = companyRepository.findById(id).orElseThrow();
        c.setStatus(CompanyStatus.REJECTED);
        companyRepository.save(c);
        return "redirect:/admin/companies";
    }

    @GetMapping("/internships")
    public String internshipApprovals(Model model) {
        List<Internship> pending = internshipRepository.findByStatus(InternshipStatus.PENDING_ADMIN_APPROVAL);
        model.addAttribute("internships", pending);
        return "admin/internship-approvals";
    }

    @GetMapping("/internships/all")
    public String allInternships(@RequestParam(required = false) InternshipStatus status, Model model) {
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
        i.setStatus(InternshipStatus.APPROVED);
        internshipRepository.save(i);
        return "redirect:/admin/internships";
    }

    @GetMapping("/internships/{id}/reject")
    public String rejectInternship(@PathVariable Long id) {
        Internship i = internshipRepository.findById(id).orElseThrow();
        i.setStatus(InternshipStatus.REJECTED);
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
                interviews.stream().filter(i -> i.getStatus() == InterviewStatus.SCHEDULED).count());
        model.addAttribute("completedCount",
                interviews.stream().filter(i -> i.getStatus() == InterviewStatus.COMPLETED).count());
        model.addAttribute("cancelledCount",
                interviews.stream().filter(i -> i.getStatus() == InterviewStatus.CANCELLED).count());
        return "admin/interview-scheduling";
    }

    @GetMapping("/schedule")
    public String scheduleForm(Model model) {
        List<Internship> approvedInternships = internshipRepository.findByStatus(InternshipStatus.APPROVED);
        model.addAttribute("approvedInternships", approvedInternships);
        model.addAttribute("candidateName", "");
        model.addAttribute("internshipId", "");
        model.addAttribute("datetime", "");
        model.addAttribute("showPopup", false);
        return "admin/schedule-form";
    }

    @PostMapping("/schedule")
    public String createInterview(@RequestParam(required = false) String candidateName,
            @RequestParam(required = false) Long internshipId,
            @RequestParam(required = false) String datetime,
            Model model) {

        List<Internship> approvedInternships = internshipRepository.findByStatus(InternshipStatus.APPROVED);
        model.addAttribute("approvedInternships", approvedInternships);

        String cleanCandidateName = candidateName == null ? "" : candidateName.trim();
        String cleanDatetime = datetime == null ? "" : datetime.trim();

        boolean hasError = false;

        model.addAttribute("candidateName", cleanCandidateName);
        model.addAttribute("internshipId", internshipId);
        model.addAttribute("datetime", cleanDatetime);

        if (cleanCandidateName.isBlank()) {
            hasError = true;
        } else if (cleanCandidateName.length() < 3 || cleanCandidateName.length() > 80) {
            hasError = true;
        } else if (!cleanCandidateName.matches("^[A-Za-z ]+$")) {
            hasError = true;
        }

        Internship selectedInternship = null;
        if (internshipId == null) {
            hasError = true;
        } else {
            selectedInternship = internshipRepository.findById(internshipId).orElse(null);
            if (selectedInternship == null || selectedInternship.getStatus() != InternshipStatus.APPROVED) {
                hasError = true;
            }
        }

        LocalDateTime interviewDateTime = null;
        if (cleanDatetime.isBlank()) {
            hasError = true;
        } else {
            try {
                interviewDateTime = LocalDateTime.parse(cleanDatetime);
                if (interviewDateTime.isBefore(LocalDateTime.now().plusMinutes(5))) {
                    hasError = true;
                }
            } catch (DateTimeParseException e) {
                hasError = true;
            }
        }

        if (hasError) {
            model.addAttribute("showPopup", true);
            model.addAttribute("formError", "Invalid input detected. Please correct the fields and try again.");
            return "admin/schedule-form";
        }

        Interview interview = new Interview();
        interview.setCandidateName(cleanCandidateName);
        interview.setInternshipTitle(selectedInternship.getTitle());
        interview.setInterviewDateTime(interviewDateTime);
        interview.setStatus(InterviewStatus.SCHEDULED);

        interviewRepository.save(interview);

        return "redirect:/admin/scheduling";
    }

    @GetMapping("/reports")
    public String reports(Model model) {

        long totalCompanies = companyRepository.count();
        long pendingCompanies = companyRepository.findByStatus(CompanyStatus.PENDING_VERIFICATION).size();
        long verifiedCompanies = companyRepository.findByStatus(CompanyStatus.VERIFIED).size();
        long rejectedCompanies = companyRepository.findByStatus(CompanyStatus.REJECTED).size();

        long pendingInternships = internshipRepository.findByStatus(InternshipStatus.PENDING_ADMIN_APPROVAL).size();
        long approvedInternships = internshipRepository.findByStatus(InternshipStatus.APPROVED).size();
        long rejectedInternships = internshipRepository.findByStatus(InternshipStatus.REJECTED).size();

        List<Interview> interviews = interviewRepository.findAll();

        long scheduledInterviews = interviews.stream()
                .filter(i -> i.getStatus() == InterviewStatus.SCHEDULED)
                .count();

        long completedInterviews = interviews.stream()
                .filter(i -> i.getStatus() == InterviewStatus.COMPLETED)
                .count();

        long cancelledInterviews = interviews.stream()
                .filter(i -> i.getStatus() == InterviewStatus.CANCELLED)
                .count();

        model.addAttribute("portalName", "UniIntern Portal");
        model.addAttribute("generatedOn", LocalDateTime.now());

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
        StringBuilder csv = new StringBuilder();

        csv.append("UniIntern Portal - Company Verification Report\n");
        csv.append("Generated On,").append(LocalDateTime.now()).append("\n\n");
        csv.append("Company Name,Email,Industry,Status,Created At\n");

        for (Company company : companyRepository.findAll()) {
            csv.append(csvEscape(company.getCompanyName())).append(",");
            csv.append(csvEscape(company.getEmail())).append(",");
            csv.append(csvEscape(company.getIndustry())).append(",");
            csv.append(company.getStatus() != null ? company.getStatus() : "").append(",");
            csv.append(company.getCreatedAt() != null ? company.getCreatedAt() : "").append("\n");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=company-report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/reports/internships/download")
    public ResponseEntity<byte[]> downloadInternshipReport() {
        StringBuilder csv = new StringBuilder();

        csv.append("UniIntern Portal - Internship Approval Report\n");
        csv.append("Generated On,").append(LocalDateTime.now()).append("\n\n");
        csv.append("Title,Location,Min GPA,Deadline,Status\n");

        for (Internship internship : internshipRepository.findAll()) {
            csv.append(csvEscape(internship.getTitle())).append(",");
            csv.append(csvEscape(internship.getLocation())).append(",");
            csv.append(internship.getMinGpa()).append(",");
            csv.append(internship.getDeadline() != null ? internship.getDeadline() : "").append(",");
            csv.append(internship.getStatus() != null ? internship.getStatus() : "").append("\n");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=internship-report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
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
    public String settings() {
        return "admin/settings";
    }
}