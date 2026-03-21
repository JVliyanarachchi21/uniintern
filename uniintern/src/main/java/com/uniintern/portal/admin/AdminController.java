package com.uniintern.portal.admin;

import com.uniintern.portal.company.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CompanyRepository companyRepository;
    private final InternshipRepository internshipRepository;

    public AdminController(CompanyRepository companyRepository, InternshipRepository internshipRepository) {
        this.companyRepository = companyRepository;
        this.internshipRepository = internshipRepository;
    }

    @GetMapping({ "/dashboard", "" })
    public String dashboard() {
        return "admin/dashboard";
    }

    // =========================
    // COMPANY APPROVALS
    // =========================
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

    // =========================
    // INTERNSHIP APPROVALS
    // =========================
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
        Internship internship = internshipRepository.findById(id).orElseThrow();
        internship.setStatus(InternshipStatus.APPROVED);
        internshipRepository.save(internship);
        return "redirect:/admin/internships";
    }

    @GetMapping("/internships/{id}/reject")
    public String rejectInternship(@PathVariable Long id) {
        Internship internship = internshipRepository.findById(id).orElseThrow();
        internship.setStatus(InternshipStatus.REJECTED);
        internshipRepository.save(internship);
        return "redirect:/admin/internships";
    }

    // =========================
    // OTHER ADMIN PAGES
    // =========================
    @GetMapping("/filtering")
    public String filteringResults() {
        return "admin/filtering-results";
    }

    @GetMapping("/scheduling")
    public String interviewScheduling() {
        return "admin/interview-scheduling";
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