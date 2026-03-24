package com.uniintern.portal.admin;

import com.uniintern.portal.company.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CompanyRepository companyRepository;
    private final InternshipRepository internshipRepository;
    private final InterviewRepository interviewRepository;

    public AdminController(CompanyRepository companyRepository,
            InternshipRepository internshipRepository,
            InterviewRepository interviewRepository) {
        this.companyRepository = companyRepository;
        this.internshipRepository = internshipRepository;
        this.interviewRepository = interviewRepository;
    }

    @GetMapping({ "/dashboard", "" })
    public String dashboard() {
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
        model.addAttribute("candidateName", "");
        model.addAttribute("internshipTitle", "");
        model.addAttribute("datetime", "");
        return "admin/schedule-form";
    }

    @PostMapping("/schedule")
    public String createInterview(@RequestParam(required = false) String candidateName,
            @RequestParam(required = false) String internshipTitle,
            @RequestParam(required = false) String datetime,
            Model model) {

        String cleanCandidateName = candidateName == null ? "" : candidateName.trim();
        String cleanInternshipTitle = internshipTitle == null ? "" : internshipTitle.trim();
        String cleanDatetime = datetime == null ? "" : datetime.trim();

        boolean hasError = false;

        model.addAttribute("candidateName", cleanCandidateName);
        model.addAttribute("internshipTitle", cleanInternshipTitle);
        model.addAttribute("datetime", cleanDatetime);

        if (cleanCandidateName.isBlank()) {
            model.addAttribute("candidateNameError", "Candidate name is required.");
            hasError = true;
        } else if (cleanCandidateName.length() < 3 || cleanCandidateName.length() > 80) {
            model.addAttribute("candidateNameError", "Candidate name must be between 3 and 80 characters.");
            hasError = true;
        } else if (!cleanCandidateName.matches("^[A-Za-z ]+$")) {
            model.addAttribute("candidateNameError", "Candidate name can contain only letters and spaces.");
            hasError = true;
        }

        if (cleanInternshipTitle.isBlank()) {
            model.addAttribute("internshipTitleError", "Internship title is required.");
            hasError = true;
        } else if (cleanInternshipTitle.length() < 3 || cleanInternshipTitle.length() > 120) {
            model.addAttribute("internshipTitleError", "Internship title must be between 3 and 120 characters.");
            hasError = true;
        } else if (!cleanInternshipTitle.matches("^[A-Za-z0-9 .,&()/-]+$")) {
            model.addAttribute("internshipTitleError", "Internship title contains invalid characters.");
            hasError = true;
        }

        LocalDateTime interviewDateTime = null;

        if (cleanDatetime.isBlank()) {
            model.addAttribute("datetimeError", "Interview date and time are required.");
            hasError = true;
        } else {
            try {
                interviewDateTime = LocalDateTime.parse(cleanDatetime);
                if (interviewDateTime.isBefore(LocalDateTime.now().plusMinutes(5))) {
                    model.addAttribute("datetimeError", "Interview time must be at least 5 minutes in the future.");
                    hasError = true;
                }
            } catch (DateTimeParseException e) {
                model.addAttribute("datetimeError", "Enter a valid interview date and time.");
                hasError = true;
            }
        }

        if (hasError) {
            model.addAttribute("formError", "Please fix the highlighted errors before scheduling the interview.");
            return "admin/schedule-form";
        }

        Interview interview = new Interview();
        interview.setCandidateName(cleanCandidateName);
        interview.setInternshipTitle(cleanInternshipTitle);
        interview.setInterviewDateTime(interviewDateTime);
        interview.setStatus(InterviewStatus.SCHEDULED);

        interviewRepository.save(interview);

        return "redirect:/admin/scheduling";
    }

    @GetMapping("/reports")
    public String reports() {
        return "admin/reports";
    }

    @GetMapping("/audit")
    public String auditLogs() {
        return "admin/audit-logs";
    }

    @GetMapping("/settings")
    public String settings() {
        return "admin/settings";
    }
}