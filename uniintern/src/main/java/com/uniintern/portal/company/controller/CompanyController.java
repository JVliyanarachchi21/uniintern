package com.uniintern.portal.company.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/company")
public class CompanyController {

    // ✅ Common values for all company pages
    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("companyName", "TechCorp Lanka");
    }

    // ✅ Dashboard
    @GetMapping({"/dashboard", "", "/"})
    public String dashboard(Model model) {

        model.addAttribute("page", "dashboard");

        // Mock stats
        model.addAttribute("activeInternships", 2);
        model.addAttribute("pendingApproval", 1);
        model.addAttribute("totalApplicants", 25);
        model.addAttribute("shortlisted", 5);
        model.addAttribute("activePromotions", 1);

        // Mock recent applicants
        List<Map<String, String>> recentApplicants = List.of(
                Map.of("name", "Ashan Fernando", "uni", "University of Colombo", "status", "Shortlisted"),
                Map.of("name", "Dilini Wickramasinghe", "uni", "University of Moratuwa", "status", "Shortlisted"),
                Map.of("name", "Nuwan Bandara", "uni", "University of Peradeniya", "status", "Shortlisted"),
                Map.of("name", "Sachini Rathnayake", "uni", "SLIIT", "status", "Shortlisted"),
                Map.of("name", "Tharaka Jayasuriya", "uni", "NSBM", "status", "Shortlisted")
        );
        model.addAttribute("recentApplicants", recentApplicants);

        // Mock notifications
        List<Map<String, String>> notifications = List.of(
                Map.of("date", "2/24/2026", "message", "Your internship 'Software Engineering Intern' has been approved!"),
                Map.of("date", "2/23/2026", "message", "Featured promotion for 'Software Engineering Intern' is now active."),
                Map.of("date", "2/22/2026", "message", "5 new applicants for 'Data Science Trainee'."),
                Map.of("date", "2/20/2026", "message", "Your internship 'QA Engineering Intern' was rejected. Reason: Incomplete description."),
                Map.of("date", "1/15/2026", "message", "Welcome to UniIntern! Complete your company profile to attract more candidates.")
        );
        model.addAttribute("notifications", notifications);

        return "company/dashboard";
    }

    // ✅ Company Profile
    @GetMapping("/profile")
public String profile(Model model) {
    model.addAttribute("page", "profile");
    model.addAttribute("companyName", "TechCorp Lanka");
    return "company/profile";
}

    // ✅ Post Internship page
    @GetMapping("/internships/new")
    public String newInternship(Model model) {
        model.addAttribute("page", "new-internship");
        return "company/new-internship";
    }

    // ✅ My Internships list page
   @GetMapping("/internships")
public String internships(Model model) {
    model.addAttribute("page", "internships");
    model.addAttribute("companyName", "TechCorp Lanka");

    List<Map<String, Object>> internships = List.of(
            Map.of("title", "Software Engineering Intern", "status", "Approved", "deadline", "2026-04-30", "applicants", 12, "featured", "Yes"),
            Map.of("title", "Data Science Trainee", "status", "Approved", "deadline", "2026-05-15", "applicants", 8, "featured", "No"),
            Map.of("title", "UI/UX Design Intern", "status", "Pending", "deadline", "2026-03-31", "applicants", 5, "featured", "No"),
            Map.of("title", "DevOps Intern", "status", "Draft", "deadline", "2026-06-30", "applicants", 0, "featured", "No"),
            Map.of("title", "QA Engineering Intern", "status", "Rejected", "deadline", "2026-04-15", "applicants", 0, "featured", "No")
    );

    model.addAttribute("internships", internships);
    model.addAttribute("page", "internships");
    return "company/internships";
}

    // ✅ Applicants page
    @GetMapping("/applicants")
public String applicants(Model model) {

    List<Map<String, Object>> applicants = List.of(
            Map.of("name", "Ashan Fernando", "gpa", 3.5, "score", 85, "status", "PENDING"),
            Map.of("name", "Dilini Wickramasinghe", "gpa", 3.8, "score", 90, "status", "SHORTLISTED"),
            Map.of("name", "Nuwan Bandara", "gpa", 3.2, "score", 70, "status", "PENDING"),
            Map.of("name", "Sachini Rathnayake", "gpa", 3.9, "score", 95, "status", "SHORTLISTED"),
            Map.of("name", "Tharaka Jayasuriya", "gpa", 3.1, "score", 65, "status", "REJECTED")
    );

    model.addAttribute("applicants", applicants);
    model.addAttribute("page", "applicants");

    return "company/applicants";
}

   

    // ✅ Promotions page
    @GetMapping("/promotions")
    public String promotions(Model model) {
        model.addAttribute("page", "promotions");
        return "company/promotions";
    }

    // ✅ Payments checkout page
    @GetMapping("/payments/checkout")
    public String checkout(Model model) {
        model.addAttribute("page", "payments");
        return "company/payments-checkout";
    }

    // ✅ Payment history page
    @GetMapping("/payments/history")
    public String paymentHistory(Model model) {
        model.addAttribute("page", "payments");
        return "company/payment-history";
    }

    // ✅ Notifications page
    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("page", "notifications");
        return "company/notifications";
    }

    // ✅ Settings page
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("page", "settings");
        return "company/settings";
    }

    @PostMapping("/internships/new")
public String submitNewInternship(
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam String location,
        @RequestParam String duration,
        @RequestParam String deadline,
        @RequestParam(required = false) String minGpa,
        @RequestParam int vacancies,
        @RequestParam(required = false) String workType,
        @RequestParam(required = false) String skills,
        @RequestParam(required = false) Integer wSkills,
        @RequestParam(required = false) Integer wGpa,
        @RequestParam(required = false) Integer wExp,
        @RequestParam(required = false) Integer wCert,
        @RequestParam(required = false) Integer topN,
        @RequestParam(required = false) String status
) {
    // UI phase: just redirect back to internships list
    // Later: save to DB and set status DRAFT/PENDING
    return "redirect:/company/internships";
}
}