package com.uniintern.portal.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({ "/dashboard", "" })
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/companies")
    public String companyApprovals() {
        return "admin/company-approvals";
    }

    @GetMapping("/internships")
    public String internshipApprovals() {
        return "admin/internship-approvals";
    }

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
