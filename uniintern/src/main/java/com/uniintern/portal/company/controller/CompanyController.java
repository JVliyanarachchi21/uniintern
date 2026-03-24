package com.uniintern.portal.company.controller;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.service.InternshipService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/company")
public class CompanyController {

    private final InternshipService internshipService;

    public CompanyController(InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("companyName", "TechCorp Lanka");
    }

    @GetMapping("/login")
             public String companyLogin() {
    return "company/company-login";
    }

    @GetMapping("/register")
             public String companyRegister() {
    return "company/company-register";
    }

    @GetMapping({"/dashboard", "", "/"})
    public String dashboard(Model model) {
        model.addAttribute("page", "dashboard");

        model.addAttribute("activeInternships", 2);
        model.addAttribute("pendingApproval", 1);
        model.addAttribute("totalApplicants", 25);
        model.addAttribute("shortlisted", 5);
        model.addAttribute("activePromotions", 1);

        List<Map<String, String>> recentApplicants = List.of(
                Map.of("name", "Ashan Fernando", "uni", "University of Colombo", "status", "Shortlisted"),
                Map.of("name", "Dilini Wickramasinghe", "uni", "University of Moratuwa", "status", "Shortlisted"),
                Map.of("name", "Nuwan Bandara", "uni", "University of Peradeniya", "status", "Shortlisted"),
                Map.of("name", "Sachini Rathnayake", "uni", "SLIIT", "status", "Shortlisted"),
                Map.of("name", "Tharaka Jayasuriya", "uni", "NSBM", "status", "Shortlisted")
        );
        model.addAttribute("recentApplicants", recentApplicants);

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
 
    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("page", "profile");
        return "company/profile";
    }

    @GetMapping("/internships/new")
    public String newInternship(Model model) {
        model.addAttribute("page", "new-internship");
        return "company/new-internship";
    }

    @GetMapping("/internships")
    public String internships(Model model) {
        model.addAttribute("page", "internships");

        Long mockCompanyId = 1L;
        List<Internship> internships = internshipService.getByCompanyId(mockCompanyId);

        model.addAttribute("internships", internships);
        return "company/internships";
    }

    @PostMapping("/internships/new")
    public String submitNewInternship(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam String duration,
            @RequestParam String deadline,
            @RequestParam(required = false) String minGpa,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String status
    ) {
        Internship internship = new Internship();

        internship.setCompanyId(1L); // later replace with logged-in company id
        internship.setTitle(title);
        internship.setDescription(description);
        internship.setLocation(location);
        internship.setDuration(duration);
        internship.setDeadline(LocalDate.parse(deadline));
        internship.setRequiredSkills(skills);

        if (minGpa != null && !minGpa.isBlank()) {
            internship.setMinGpa(Double.parseDouble(minGpa));
        }

        if ("DRAFT".equalsIgnoreCase(status)) {
            internship.setStatus("DRAFT");
        } else {
            internship.setStatus("PENDING_ADMIN_APPROVAL");
        }

        internship.setCreatedAt(LocalDateTime.now());

        internshipService.save(internship);

        return "redirect:/company/internships";
    }

    @GetMapping("/applicants")
    public String applicants(Model model) {
        model.addAttribute("page", "applicants");

        List<Map<String, Object>> applicants = List.of(
                Map.of("name", "Ashan Fernando", "university", "University of Colombo", "gpa", 3.25, "skillMatch", "71%", "score", 79, "status", "Shortlisted"),
                Map.of("name", "Dilini Wickramasinghe", "university", "University of Moratuwa", "gpa", 3.81, "skillMatch", "62%", "score", 73, "status", "Shortlisted"),
                Map.of("name", "Nuwan Bandara", "university", "University of Peradeniya", "gpa", 3.42, "skillMatch", "68%", "score", 96, "status", "Shortlisted"),
                Map.of("name", "Sachini Rathnayake", "university", "SLIIT", "gpa", 2.53, "skillMatch", "51%", "score", 46, "status", "Shortlisted"),
                Map.of("name", "Tharaka Jayasuriya", "university", "NSBM", "gpa", 3.24, "skillMatch", "97%", "score", 42, "status", "Shortlisted")
        );

        model.addAttribute("applicants", applicants);
        return "company/applicants";
    }

    @GetMapping("/promotions")
    public String promotions(Model model) {
        model.addAttribute("page", "promotions");
        return "company/promotions";
    }

    @GetMapping("/payments/checkout")
    public String checkout(Model model) {
        model.addAttribute("page", "payments");
        return "company/payments-checkout";
    }

    @GetMapping("/payments/history")
    public String paymentHistory(Model model) {
        model.addAttribute("page", "payments");

        List<Map<String, Object>> payments = List.of(
                Map.of("id", "PAY-001", "date", "2026-01-20", "plan", "Featured Internship — 7 Days", "amount", "LKR 5,000", "status", "Active"),
                Map.of("id", "PAY-002", "date", "2025-12-01", "plan", "Featured Internship — 14 Days", "amount", "LKR 8,500", "status", "Expired")
        );

        model.addAttribute("payments", payments);
        return "company/payment-history";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("page", "notifications");

        List<Map<String, String>> notifications = List.of(
                Map.of("date", "2/24/2026, 10:30:00 AM", "message", "Your internship 'Software Engineering Intern' has been approved!"),
                Map.of("date", "2/23/2026, 2:00:00 PM", "message", "Featured promotion for 'Software Engineering Intern' is now active."),
                Map.of("date", "2/22/2026, 9:15:00 AM", "message", "5 new applicants for 'Data Science Trainee'."),
                Map.of("date", "2/20/2026, 4:45:00 PM", "message", "Your internship 'QA Engineering Intern' was rejected. Reason: Incomplete description."),
                Map.of("date", "1/15/2026, 8:00:00 AM", "message", "Welcome to UniIntern! Complete your company profile to attract more candidates.")
        );

        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", 0);

        return "company/notifications";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("page", "settings");
        return "company/settings";
    }

    @GetMapping("/internships/edit/{id}")
public String editInternship(@PathVariable Long id, Model model) {

    Internship internship = internshipService.getById(id);

    model.addAttribute("internship", internship);
    model.addAttribute("page", "internships");

    return "company/edit-internships";
}
  
@PostMapping("/internships/update")
public String updateInternship(
        @RequestParam Long id,
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam String location,
        @RequestParam String duration,
        @RequestParam(required = false) String minGpa,
        @RequestParam String deadline,
        @RequestParam(required = false) String requiredSkills
) {
    Internship internship = internshipService.getById(id);

    internship.setTitle(title);
    internship.setDescription(description);
    internship.setLocation(location);
    internship.setDuration(duration);
    internship.setRequiredSkills(requiredSkills);
    internship.setDeadline(java.time.LocalDate.parse(deadline));

    if (minGpa != null && !minGpa.isBlank()) {
        internship.setMinGpa(Double.parseDouble(minGpa));
    } else {
        internship.setMinGpa(null);
    }

    internshipService.save(internship);

    return "redirect:/company/internships/view/" + internship.getId();
}


@GetMapping("/internships/delete/{id}")
public String deleteInternship(@PathVariable Long id) {
    internshipService.delete(id);
    return "redirect:/company/internships";
}

@GetMapping("/internships/view/{id}")
public String viewInternship(@PathVariable Long id,
                             @RequestParam(defaultValue = "overview") String tab,
                             Model model) {

    Internship internship = internshipService.getById(id);

    model.addAttribute("internship", internship);
    model.addAttribute("page", "internships");
    model.addAttribute("activeTab", tab);

    List<Map<String, Object>> applicants = List.of(
            Map.of("name", "Ashan Fernando", "university", "University of Colombo", "gpa", 3.75, "score", 54, "status", "Shortlisted"),
            Map.of("name", "Dilini Wickramasinghe", "university", "University of Moratuwa", "gpa", 3.48, "score", 40, "status", "Shortlisted"),
            Map.of("name", "Nuwan Bandara", "university", "University of Peradeniya", "gpa", 3.33, "score", 96, "status", "Shortlisted"),
            Map.of("name", "Sachini Rathnayake", "university", "SLIIT", "gpa", 2.90, "score", 88, "status", "Scored")
    );

    model.addAttribute("applicants", applicants);

    return "company/view-internship";
}

}

