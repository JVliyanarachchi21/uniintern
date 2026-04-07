package com.uniintern.portal.company.controller;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.service.InternshipService;
import com.uniintern.portal.company.service.CompanyService;
import com.uniintern.portal.company.service.PromotionService;
import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.dto.CompanyRegistrationDto;
import com.uniintern.portal.company.dto.InternshipListingDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/company")
public class CompanyController {

    private final InternshipService internshipService;
    private final CompanyService companyService;
    private final PromotionService promotionService;

    public CompanyController(InternshipService internshipService, CompanyService companyService, PromotionService promotionService) {
        this.internshipService = internshipService;
        this.companyService = companyService;
        this.promotionService = promotionService;
    }

    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        if (session != null) {
            Long companyId = (Long) session.getAttribute("loggedInCompanyId");
            if (companyId != null) {
                Company company = companyService.findById(companyId);
                if (company != null) {
                    model.addAttribute("companyName", company.getCompanyName());
                    model.addAttribute("companyLogo", company.getLogoPath() != null ? company.getLogoPath() : "");
                    
                    long pendingCount = internshipService.countByCompanyIdAndStatus(companyId, "PENDING_ADMIN_APPROVAL");
                    long promoCount = promotionService.countActivePromotionsForCompany(companyId);
                    long approvedCount = internshipService.countNewlyApproved(companyId);
                    long rejectedCount = internshipService.countNewlyRejected(companyId);
                    
                    long totalNotifications = 0;
                    if (pendingCount > 0) totalNotifications++;
                    if (promoCount > 0) totalNotifications++;
                    totalNotifications += approvedCount;
                    totalNotifications += rejectedCount;
                    
                    if (totalNotifications == 0) totalNotifications = 1; // Welcome msg
                    
                    Boolean notificationsRead = (Boolean) session.getAttribute("notificationsRead");
                    if (notificationsRead != null && notificationsRead && approvedCount == 0 && rejectedCount == 0) {
                        model.addAttribute("unreadCount", 0);
                    } else {
                        model.addAttribute("unreadCount", totalNotifications);
                    }
                }
            }
        }
    }

    @GetMapping("/login")
    public String companyLogin(@RequestParam(value = "verified", required = false) String verified,
                               @RequestParam(value = "success", required = false) String success,
                               Model model) {
        if ("true".equals(verified)) {
            model.addAttribute("message", "Email verified successfully! Your account is now pending admin approval.");
        } else if (success != null) {
            model.addAttribute("message", "Registration successful! Please verify your email or log in.");
        }
        return "company/company-login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        Company company = companyService.findByEmail(email);
        
        if (company == null || !company.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid email or password");
            return "company/company-login";
        }
        
        if ("PENDING_VERIFICATION".equals(company.getStatus())) {
            model.addAttribute("error", "Please verify your email to continue.");
            return "company/company-login";
        }
        
        if ("PENDING_APPROVAL".equals(company.getStatus())) {
            model.addAttribute("error", "Your account is pending admin approval.");
            return "company/company-login";
        }
        
        if (!"APPROVED".equals(company.getStatus()) && !"ACTIVE".equals(company.getStatus())) {
            model.addAttribute("error", "Your account is currently disabled.");
            return "company/company-login";
        }
        
        session.setAttribute("loggedInCompanyId", company.getId());
        return "redirect:/company/dashboard";
    }

    @GetMapping("/register")
    public String companyRegister() {
        return "company/company-register";
    }

    @PostMapping("/register")
    public String registerCompany(@ModelAttribute CompanyRegistrationDto dto, Model model) {
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
        if (email != null) model.addAttribute("email", email);
        return "company/company-verify";
    }

    @PostMapping("/verify")
    public String handleVerifyOtp(@RequestParam("email") String email, @RequestParam("otp") String otp, Model model) {
        try {
            companyService.verifyOtp(email, otp);
            model.addAttribute("verified", true);
            return "company/company-verify";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "company/company-verify";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "company/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            companyService.generatePasswordResetOtp(email);
            model.addAttribute("email", email);
            return "redirect:/company/verify-reset-otp?email=" + email;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "company/forgot-password";
        }
    }

    @GetMapping("/verify-reset-otp")
    public String verifyResetOtpPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "company/verify-reset-otp";
    }

    @PostMapping("/verify-reset-otp")
    public String handleVerifyResetOtp(@RequestParam("email") String email, @RequestParam("otp") String otp, Model model) {
        try {
            companyService.verifyPasswordResetOtp(email, otp);
            model.addAttribute("email", email);
            model.addAttribute("otp", otp);
            return "redirect:/company/reset-password?email=" + email + "&otp=" + otp;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "company/verify-reset-otp";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("email") String email, @RequestParam("otp") String otp, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("otp", otp);
        return "company/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("email") String email, @RequestParam("otp") String otp, @RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword, Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("email", email);
            model.addAttribute("otp", otp);
            return "company/reset-password";
        }
        try {
            companyService.resetPassword(email, newPassword);
            model.addAttribute("message", "Password reset successful! Please log in with your new password.");
            return "redirect:/company/login?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            model.addAttribute("otp", otp);
            return "company/reset-password";
        }
    }

    @GetMapping({"/dashboard", "", "/"})
    public String dashboard(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        if (companyId == null) return "redirect:/company/login";

        model.addAttribute("page", "dashboard");
        Company company = companyService.findById(companyId);
        if (company == null) return "redirect:/company/login";
        model.addAttribute("company", company);

        long activeInternships = internshipService.countByCompanyIdAndStatus(companyId, "APPROVED");
        long pendingApproval = internshipService.countByCompanyIdAndStatus(companyId, "PENDING_ADMIN_APPROVAL");
        long activePromotions = promotionService.countActivePromotionsForCompany(companyId);

        model.addAttribute("activeInternships", activeInternships);
        model.addAttribute("pendingApproval", pendingApproval);
        model.addAttribute("totalApplicants", 0);
        model.addAttribute("shortlisted", 0);
        model.addAttribute("activePromotions", activePromotions);

        // Recent Internships
        List<Internship> recent = internshipService.getByCompanyId(companyId).stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentActivities", recent);

        // Persistent Notifications for Dashboard (Latest 5)
        List<Map<String, Object>> activities = internshipService.getActivityNotifications(companyId);
        model.addAttribute("notifications", activities.stream().limit(5).collect(Collectors.toList()));

        return "company/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        if (companyId == null) return "redirect:/company/login";
        model.addAttribute("page", "profile");
        model.addAttribute("company", companyService.findById(companyId));
        return "company/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("companyName") String companyName,
                               @RequestParam("industry") String industry,
                               @RequestParam("email") String email,
                               @RequestParam("phone") String phone,
                               @RequestParam("website") String website,
                               @RequestParam("address") String address,
                               @RequestParam("description") String description,
                               @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        if (companyId == null) return "redirect:/company/login";

        String logoPath = null;
        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                String uploadDir = "uploads/logos/";
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists()) dir.mkdirs();
                String fileName = UUID.randomUUID().toString() + "_" + logoFile.getOriginalFilename();
                java.nio.file.Files.copy(logoFile.getInputStream(), java.nio.file.Paths.get(uploadDir, fileName));
                logoPath = "/uploads/logos/" + fileName;
            } catch (Exception e) {}
        }

        try {
            companyService.updateProfile(companyId, companyName, industry, email, phone, website, address, description, logoPath);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/company/profile";
    }

    @GetMapping("/internships")
    public String internships(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        if (companyId == null) return "redirect:/company/login";
        model.addAttribute("page", "internships");
        model.addAttribute("internships", internshipService.getByCompanyId(companyId));
        model.addAttribute("promotedIds", new HashSet<>(promotionService.getPromotedInternshipIds()));
        return "company/internships";
    }

    @GetMapping("/internships/new")
    public String newInternship(Model model) {
        model.addAttribute("page", "new-internship");
        return "company/new-internship";
    }

    @PostMapping("/internships/new")
    public String submitNewInternship(@RequestParam Map<String, String> params, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        Internship i = new Internship();
        i.setCompanyId(companyId);
        i.setTitle(params.get("title"));
        i.setDescription(params.get("description"));
        i.setLocation(params.get("location"));
        i.setDuration(params.get("duration"));
        i.setDeadline(LocalDate.parse(params.get("deadline")));
        i.setRequiredSkills(params.get("skills"));
        i.setStatus("DRAFT".equalsIgnoreCase(params.get("status")) ? "DRAFT" : "PENDING_ADMIN_APPROVAL");
        i.setCreatedAt(LocalDateTime.now());
        internshipService.save(i);
        session.setAttribute("notificationsRead", false);
        return "redirect:/company/internships";
    }

    @GetMapping("/notifications")
    public String notifications(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        if (companyId == null) return "redirect:/company/login";
        model.addAttribute("page", "notifications");

        // Fetch all persistent activity notifications
        List<Map<String, Object>> allNotifications = internshipService.getActivityNotifications(companyId);
        
        // Add passive status notifications (always "New" in a sense, but not tracked in DB)
        long pending = internshipService.countByCompanyIdAndStatus(companyId, "PENDING_ADMIN_APPROVAL");
        long activePromos = promotionService.countActivePromotionsForCompany(companyId);
        
        List<Map<String, Object>> displayList = new ArrayList<>(allNotifications);
        
        if (pending > 0) {
            displayList.add(Map.of("date", "Today", "message", "You have " + pending + " internship(s) pending approval.", "type", "info", "icon", "bi-clock-history", "isNew", false));
        }
        if (activePromos > 0) {
            displayList.add(Map.of("date", "Recent", "message", "You have " + activePromos + " active promotion(s) running.", "type", "info", "icon", "bi-star", "isNew", false));
        }
        if (displayList.isEmpty()) {
            displayList.add(Map.of("date", "Welcome", "message", "Welcome to UniIntern! Post your first internship to get started.", "type", "info", "icon", "bi-info-circle", "isNew", false));
        }

        model.addAttribute("notifications", displayList);
        
        // Count ONLY the unread activity notifications for the header sub-text
        long unreadCount = allNotifications.stream().filter(n -> (Boolean)n.get("isNew")).count();
        model.addAttribute("unreadCount", unreadCount);
        
        // Mark everything as seen in session and DB
        session.setAttribute("notificationsRead", true);
        internshipService.markApprovedAsSeen(companyId);
        internshipService.markRejectedAsSeen(companyId);

        return "company/notifications";
    }

    @GetMapping("/settings")
    public String settings(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        if (companyId == null) return "redirect:/company/login";
        model.addAttribute("page", "settings");
        model.addAttribute("company", companyService.findById(companyId));
        return "company/settings";
    }

    @PostMapping("/settings/update-password")
    public String updateSettingsPassword(@RequestParam("currentPassword") String current, @RequestParam("newPassword") String next, HttpSession session, RedirectAttributes ra) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        try {
            companyService.updatePassword(companyId, current, next);
            ra.addFlashAttribute("success", "Password updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/company/settings?tab=security";
    }

    @GetMapping("/internships/edit/{id}")
    public String editInternship(@PathVariable("id") Long id, Model model) {
        model.addAttribute("internship", internshipService.getById(id));
        model.addAttribute("page", "internships");
        return "company/edit-internships";
    }

    @PostMapping("/internships/update")
    public String updateInternship(@RequestParam Map<String, String> params) {
        Internship i = internshipService.getById(Long.parseLong(params.get("id")));
        i.setTitle(params.get("title"));
        i.setDescription(params.get("description"));
        i.setLocation(params.get("location"));
        i.setDuration(params.get("duration"));
        i.setDeadline(LocalDate.parse(params.get("deadline")));
        i.setRequiredSkills(params.get("requiredSkills"));
        if ("submit".equals(params.get("action"))) i.setStatus("PENDING_ADMIN_APPROVAL");
        internshipService.save(i);
        return "submit".equals(params.get("action")) ? "redirect:/company/internships" : "redirect:/company/internships/view/" + i.getId();
    }

    @GetMapping("/internships/delete/{id}")
    public String deleteInternship(@PathVariable("id") Long id) {
        internshipService.delete(id);
        return "redirect:/company/internships";
    }

    @GetMapping("/internships/view/{id}")
    public String viewInternship(@PathVariable("id") Long id, @RequestParam(name = "tab", defaultValue = "overview") String tab, Model model) {
        model.addAttribute("internship", internshipService.getById(id));
        model.addAttribute("page", "internships");
        model.addAttribute("activeTab", tab);
        model.addAttribute("applicants", List.of(
            Map.of("name", "Ashan Fernando", "university", "UoC", "gpa", 3.75, "status", "Shortlisted"),
            Map.of("name", "Dilini Wickramasinghe", "university", "UoM", "gpa", 3.48, "status", "Shortlisted")
        ));
        return "company/view-internship";
    }
    
    @GetMapping("/payments/checkout")
    public String checkout(@RequestParam("internshipId") Long id, @RequestParam("type") String type, @RequestParam("price") Double price, @RequestParam("days") Integer days, Model model) {
        model.addAttribute("page", "promotions");
        model.addAttribute("internship", internshipService.getById(id));
        model.addAttribute("promoType", type);
        model.addAttribute("price", price);
        model.addAttribute("days", days);
        model.addAttribute("internshipId", id);
        return "company/payments-checkout";
    }

    @PostMapping("/payments/complete")
    public String completePayment(@RequestParam("internshipId") Long id, @RequestParam("type") String type, @RequestParam("price") Double price, @RequestParam("days") Integer days, RedirectAttributes ra, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        promotionService.createPromotion(id, companyId, type, price, days);
        ra.addAttribute("internshipId", id);
        ra.addAttribute("type", type);
        return "redirect:/company/payments/success";
    }

    @GetMapping("/payments/success")
    public String paymentSuccess(@RequestParam("internshipId") Long id, @RequestParam("type") String type, Model model) {
        model.addAttribute("page", "promotions");
        model.addAttribute("internship", internshipService.getById(id));
        model.addAttribute("promoType", type);
        return "company/payment-success";
    }

    @GetMapping("/payments/history")
    public String paymentHistory(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        model.addAttribute("page", "payments");
        model.addAttribute("payments", promotionService.getAllPromotionsForCompany(companyId).stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", "PAY-" + p.getId());
            map.put("date", p.getStartDate().toLocalDate().toString());
            map.put("plan", p.getType());
            map.put("amount", "LKR " + p.getPrice());
            map.put("status", p.getStatus());
            return map;
        }).collect(Collectors.toList()));
        return "company/payment-history";
    }

    @GetMapping("/promotions")
    public String promotions(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        model.addAttribute("page", "promotions");
        List<InternshipListingDto> internships = internshipService.getApprovedInternshipsListings(null, companyId, null);
        Set<Long> promotedIds = new HashSet<>(promotionService.getPromotedInternshipIds());
        model.addAttribute("internships", internships.stream().filter(i -> !promotedIds.contains(i.getId())).collect(Collectors.toList()));
        return "company/promotions";
    }

    @PostMapping("/promotions/process")
    public String processPromotion(@RequestParam("internshipId") Long id, @RequestParam("type") String type, @RequestParam("price") Double price, @RequestParam("days") Integer days, RedirectAttributes ra) {
        ra.addAttribute("internshipId", id);
        ra.addAttribute("type", type);
        ra.addAttribute("price", price);
        ra.addAttribute("days", days);
        return "redirect:/company/payments/checkout";
    }
    
    @GetMapping("/applicants")
    public String applicants(Model model) {
        model.addAttribute("page", "applicants");
        model.addAttribute("applicants", List.of(
            Map.of("name", "Ashan Fernando", "university", "UoC", "gpa", 3.25, "score", 79, "status", "Shortlisted")
        ));
        return "company/applicants";
    }
}
