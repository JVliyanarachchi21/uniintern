package com.uniintern.portal.company.controller;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.service.InternshipService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/company")
public class CompanyController {

    private final InternshipService internshipService;
    private final com.uniintern.portal.company.service.CompanyService companyService;
    private final com.uniintern.portal.company.service.PromotionService promotionService;

    public CompanyController(InternshipService internshipService, com.uniintern.portal.company.service.CompanyService companyService, com.uniintern.portal.company.service.PromotionService promotionService) {
        this.internshipService = internshipService;
        this.companyService = companyService;
        this.promotionService = promotionService;
    }

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        try {
            com.uniintern.portal.company.entity.Company company = companyService.getOrCreateMockCompany();
            model.addAttribute("companyName", company.getCompanyName());
            model.addAttribute("companyLogo", company.getLogoPath() != null ? company.getLogoPath() : "");
        } catch (Exception e) {
            model.addAttribute("companyName", "TechCorp Lanka");
            model.addAttribute("companyLogo", "");
        }
    }

    @GetMapping("/login")
    public String companyLogin(@RequestParam(value = "verified", required = false) String verified,
                               @RequestParam(value = "success", required = false) String success,
                               Model model) {
        if ("true".equals(verified)) {
            model.addAttribute("message", "Email verified successfully! You can now log in.");
        } else if (success != null) {
            model.addAttribute("message", "Registration successful! Please verify your email or log in.");
        }
        return "company/company-login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email, @RequestParam("password") String password, Model model) {
        if ("hr@techcorp.lk".equals(email) && "password123".equals(password)) {
            return "redirect:/company/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "company/company-login";
        }
    }

    @GetMapping("/register")
    public String companyRegister() {
        return "company/company-register";
    }

    @PostMapping("/register")
    public String registerCompany(@ModelAttribute com.uniintern.portal.company.dto.CompanyRegistrationDto dto, Model model) {
        try {
            companyService.registerCompany(dto);
            return "redirect:/company/verify?email=" + dto.getEmail();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "company/company-register";
        }
    }
    
    @GetMapping("/verify")
    public String verifyPageString(@RequestParam(value = "email", required = false) String email, Model model) {
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "company/company-verify";
    }

    @PostMapping("/verify")
    public String handleVerifyOtp(@RequestParam("email") String email, @RequestParam("otp") String otp, Model model) {
        try {
            companyService.verifyOtp(email, otp);
            return "redirect:/company/login?verified=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "company/company-verify";
        }
    }

    @GetMapping("/internships/preview")
    public String internshipPreview(@RequestParam(value = "id", required = false) Long id, Model model) {
        if (id == null) {
            return "redirect:/company/internships/listing";
        }
        
        com.uniintern.portal.company.entity.Internship internship = internshipService.getById(id);
        if (internship == null) {
            return "redirect:/company/internships/listing";
        }
        
        com.uniintern.portal.company.entity.Company company = companyService.findById(internship.getCompanyId());
        
        model.addAttribute("internship", internship);
        model.addAttribute("company", company);
        model.addAttribute("page", "internships");
        return "company/internship-preview";
    }

@GetMapping("/internships/application-template")
public String applicationTemplate(Model model) {
    model.addAttribute("page", "internships");
    return "company/application-template";
}

@GetMapping("/internships/listing")
public String internshipsListing(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "companyId", required = false) Long companyId,
        @RequestParam(value = "type", required = false) String type,
        Model model) {
    java.util.List<com.uniintern.portal.company.dto.InternshipListingDto> internships = internshipService.getApprovedInternshipsListings(keyword, companyId, type);
    model.addAttribute("internships", internships);
    model.addAttribute("companies", companyService.getAllCompanies());
    model.addAttribute("keyword", keyword);
    model.addAttribute("companyId", companyId);
    model.addAttribute("type", type);
    return "company/internships-listing";
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
        try {
            com.uniintern.portal.company.entity.Company company = companyService.getOrCreateMockCompany();
            model.addAttribute("company", company);
        } catch (Exception e) {
            // Provide a dummy company if completely fails
            // ...
        }
        return "company/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam("companyName") String companyName,
            @RequestParam("industry") String industry,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("website") String website,
            @RequestParam("address") String address,
            @RequestParam("description") String description,
            @RequestParam(value = "logoFile", required = false) org.springframework.web.multipart.MultipartFile logoFile,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes
    ) {
        Long mockCompanyId = 1L;
        String logoPath = null;

        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                String uploadDir = "uploads/logos/";
                java.io.File uploadDirFile = new java.io.File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                String fileName = java.util.UUID.randomUUID().toString() + "_" + logoFile.getOriginalFilename();
                java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir, fileName);
                java.nio.file.Files.copy(logoFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                logoPath = "/uploads/logos/" + fileName;
            } catch (java.io.IOException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload logo: " + e.getMessage());
                return "redirect:/company/profile";
            }
        }

        try {
            companyService.updateProfile(mockCompanyId, companyName, industry, email, phone, website, address, description, logoPath);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/company/profile";
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
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("duration") String duration,
            @RequestParam("deadline") String deadline,
            @RequestParam(value = "minGpa", required = false) String minGpa,
            @RequestParam(value = "maxGpa", required = false) String maxGpa,
            @RequestParam(value = "skills", required = false) String skills,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "wSkills", required = false) Integer wSkills,
            @RequestParam(value = "wGpa", required = false) Integer wGpa,
            @RequestParam(value = "wExp", required = false) Integer wExp,
            @RequestParam(value = "wCert", required = false) Integer wCert,
            @RequestParam(value = "topN", required = false) Integer topN
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
        
        if (maxGpa != null && !maxGpa.isBlank()) {
            internship.setMaxGpa(Double.parseDouble(maxGpa));
        }

        if ("DRAFT".equalsIgnoreCase(status)) {
            internship.setStatus("DRAFT");
        } else {
            internship.setStatus("PENDING_ADMIN_APPROVAL");
        }

        internship.setSkillsWeight(wSkills != null ? wSkills : 0);
        internship.setGpaWeight(wGpa != null ? wGpa : 0);
        internship.setExperienceWeight(wExp != null ? wExp : 0);
        internship.setCertificatesWeight(wCert != null ? wCert : 0);
        internship.setTopNCandidates(topN != null ? topN : 10);

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
        
        // Fetch real approved internships for the dropdown
        List<com.uniintern.portal.company.dto.InternshipListingDto> internships = internshipService.getApprovedInternshipsListings(null, null, null);
        model.addAttribute("internships", internships);
        
        return "company/promotions";
    }

    @PostMapping("/promotions/process")
    public String processPromotion(@RequestParam("internshipId") Long internshipId,
                                   @RequestParam(value = "type", required = false, defaultValue = "Featured Internship") String type,
                                   @RequestParam(value = "price", required = false, defaultValue = "5000.0") Double price,
                                   @RequestParam(value = "days", required = false, defaultValue = "7") Integer days,
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        
        System.out.println("Processing promotion for Internship: " + internshipId + ", Plan: " + type + ", Price: " + price);
        
        // Redirect to checkout page with parameters
        redirectAttributes.addAttribute("internshipId", internshipId);
        redirectAttributes.addAttribute("type", type);
        redirectAttributes.addAttribute("price", price);
        redirectAttributes.addAttribute("days", days);
        
        System.out.println("Redirecting to /company/payments/checkout");
        return "redirect:/company/payments/checkout";
    }

    @PostMapping("/payments/complete")
    public String completePayment(
            @RequestParam("internshipId") Long internshipId,
            @RequestParam("type") String type,
            @RequestParam("price") Double price,
            @RequestParam("days") Integer days,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        
        // Finalize transaction and activate promotion
        Long mockCompanyId = 1L;
        promotionService.createPromotion(internshipId, mockCompanyId, type, price, days);
        
        redirectAttributes.addAttribute("internshipId", internshipId);
        redirectAttributes.addAttribute("type", type);
        
        return "redirect:/company/payments/success";
    }

    @GetMapping("/payments/success")
    public String paymentSuccess(
            @RequestParam("internshipId") Long internshipId,
            @RequestParam("type") String type,
            Model model) {
        
        model.addAttribute("page", "promotions");
        model.addAttribute("internship", internshipService.getById(internshipId));
        model.addAttribute("promoType", type);
        
        return "company/payment-success";
    }

    @GetMapping("/payments/checkout")
    public String checkout(
            @RequestParam("internshipId") Long internshipId,
            @RequestParam("type") String type,
            @RequestParam("price") Double price,
            @RequestParam("days") Integer days,
            Model model) {
        model.addAttribute("page", "promotions");
        model.addAttribute("internship", internshipService.getById(internshipId));
        model.addAttribute("promoType", type);
        model.addAttribute("price", price);
        model.addAttribute("days", days);
        model.addAttribute("internshipId", internshipId);
        return "company/payments-checkout";
    }

    @GetMapping("/payments/history")
    public String paymentHistory(Model model) {
        model.addAttribute("page", "payments");

        Long mockCompanyId = 1L;
        List<com.uniintern.portal.company.entity.Promotion> promos = promotionService.getAllPromotionsForCompany(mockCompanyId);
        
        List<Map<String, Object>> payments = promos.stream().map(p -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", "PAY-" + String.format("%03d", p.getId()));
            map.put("date", p.getStartDate().toLocalDate().toString());
            
            // Fetch internship title
            String internTitle = "Unknown Internship";
            try {
                com.uniintern.portal.company.entity.Internship i = internshipService.getById(p.getInternshipId());
                if(i != null) internTitle = i.getTitle();
            } catch(Exception e) {}
            
            map.put("plan", p.getType() + " (" + internTitle + ")");
            map.put("amount", "LKR " + String.format("%,.0f", p.getPrice()));
            map.put("status", p.getStatus()); // Will be "ACTIVE"
            return map;
        }).collect(java.util.stream.Collectors.toList());

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
public String editInternship(@PathVariable("id") Long id, Model model) {

    Internship internship = internshipService.getById(id);

    model.addAttribute("internship", internship);
    model.addAttribute("page", "internships");

    return "company/edit-internships";
}
  
@PostMapping("/internships/update")
public String updateInternship(
        @RequestParam("id") Long id,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("location") String location,
        @RequestParam("duration") String duration,
        @RequestParam(value = "minGpa", required = false) String minGpa,
        @RequestParam(value = "maxGpa", required = false) String maxGpa,
        @RequestParam("deadline") String deadline,
        @RequestParam(value = "requiredSkills", required = false) String requiredSkills
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
    
    if (maxGpa != null && !maxGpa.isBlank()) {
        internship.setMaxGpa(Double.parseDouble(maxGpa));
    } else {
        internship.setMaxGpa(null);
    }

    internshipService.save(internship);

    return "redirect:/company/internships/view/" + internship.getId();
}


@GetMapping("/internships/delete/{id}")
public String deleteInternship(@PathVariable("id") Long id) {
    internshipService.delete(id);
    return "redirect:/company/internships";
}

@GetMapping("/internships/view/{id}")
public String viewInternship(@PathVariable("id") Long id,
                             @RequestParam(name = "tab", defaultValue = "overview") String tab,
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

