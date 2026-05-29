package com.uniintern.portal.cv_filering.controller;

import com.uniintern.portal.cv_filering.model.*;
import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.student.model.StudentApplication;
import com.uniintern.portal.cv_filering.service.DashboardService;
import com.uniintern.portal.cv_filering.repo.FilteringLogRepository;
import com.uniintern.portal.company.service.CompanyService;
import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.service.InternshipService;
import com.uniintern.portal.company.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/company/cv")
public class PageController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private FilteringLogRepository filteringLogRepository;
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private InternshipService internshipService;
    
    @Autowired
    private PromotionService promotionService;
    
    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        if (session != null) {
            Long companyId = (Long) session.getAttribute("loggedInCompanyId");
            if (companyId != null) {
                Company company = companyService.findById(companyId);
                if (company != null) {
                    model.addAttribute("companyName", company.getCompanyName());
                    model.addAttribute("companyLogo", company.getLogoPath() != null ? company.getLogoPath() : "");
                    
                    // Add notification icons if same logic as company portal
                    long pendingCount = internshipService.countByCompanyIdAndStatus(companyId, "PENDING_ADMIN_APPROVAL");
                    long promoCount = promotionService.countActivePromotionsForCompany(companyId);
                    long approvedCount = internshipService.countNewlyApproved(companyId);
                    long rejectedCount = internshipService.countNewlyRejected(companyId);
                    
                    long totalNotifications = 0;
                    if (pendingCount > 0) totalNotifications++;
                    if (promoCount > 0) totalNotifications++;
                    totalNotifications += (approvedCount + rejectedCount);
                    
                    model.addAttribute("totalNotifications", totalNotifications);
                }
            }
        }
    }
    
    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/company/cv/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        model.addAttribute("readyInternshipsCount", dashboardService.getReadyInternshipsCount(companyId));
        model.addAttribute("missingGuidesCount", dashboardService.getMissingGuidesCount(companyId));
        model.addAttribute("awaitingDeadlineCount", dashboardService.getAwaitingDeadlineCount(companyId));
        model.addAttribute("completedRunsCount", dashboardService.getCompletedRunsCount(companyId));
        model.addAttribute("pendingApprovalsCount", dashboardService.getPendingApprovalsCount(companyId));
        model.addAttribute("recentRuns", dashboardService.getRecentRuns(5, companyId));
        model.addAttribute("userRole", "company");
        model.addAttribute("companyId", companyId);
        return "cv_filtering/dashboard";
    }
    
    @GetMapping("/marking-guides")
    public String markingGuides(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        model.addAttribute("internships", dashboardService.getInternshipsByCompanyId(companyId));
        model.addAttribute("userRole", "company");
        return "cv_filtering/marking-guides";
    }
    
    @GetMapping("/marking-guide-detail/{id}")
    public String markingGuideDetail(@PathVariable String id, Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        Internship guide = dashboardService.getInternshipById(id);
        
        // Verify internship belongs to company
        if (guide != null && !guide.getCompanyId().equals(companyId)) {
            return "redirect:/company/cv/marking-guides";
        }
        
        model.addAttribute("guide", guide);
        model.addAttribute("userRole", "company");
        return "cv_filtering/marking-guide-detail";
    }
    
    @GetMapping("/run-filtering")
    public String runFiltering(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        // Show ALL approved internships, not just those with weights set
        model.addAttribute("internships", dashboardService.getAllApprovedInternshipsByCompanyId(companyId));
        model.addAttribute("userRole", "company");
        return "cv_filtering/run-filtering";
    }
    
    @PostMapping("/run-filtering")
    public String runFilteringPost(@RequestParam String internshipId, 
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        String companyName = (String) session.getAttribute("companyName");
        
        // Check if internship has weights configured
        try {
            Long id = Long.parseLong(internshipId);
            var internship = dashboardService.getInternshipById(internshipId);
            if (internship != null && internship.getSkillsWeight() == null) {
                redirectAttributes.addFlashAttribute("error", 
                    "Cannot run filtering: Marking guide weights are not set for this internship. Please configure weights first.");
                return "redirect:/company/cv/run-filtering";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid internship selected.");
            return "redirect:/company/cv/run-filtering";
        }
        
        FilterRun run = dashboardService.runFilteringForCompany(internshipId, companyId, companyName);
        if (run != null) {
            // Log the filtering run
            FilteringLog log = new FilteringLog(
                companyName != null ? companyName : "Company " + companyId,
                "company",
                companyId,
                run.getInternshipId(),
                run.getInternshipTitle(),
                LocalDateTime.now(),
                run.getApplicantsProcessed(),
                "success"
            );
            filteringLogRepository.save(log);
            
            redirectAttributes.addFlashAttribute("message", "Filtering completed successfully!");
            return "redirect:/company/cv/results";
        }
        redirectAttributes.addFlashAttribute("error", "Failed to run filtering.");
        return "redirect:/company/cv/run-filtering";
    }
    
    @GetMapping("/applicants-preview")
    public String applicantsPreview(@RequestParam String internshipId, Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        Internship internship = dashboardService.getInternshipById(internshipId);
        
        // Verify internship belongs to company
        if (internship != null && !internship.getCompanyId().equals(companyId)) {
            return "redirect:/company/cv/run-filtering";
        }
        
        List<StudentApplication> applications = dashboardService.getApplicationsByInternshipId(internshipId);
        model.addAttribute("internship", internship);
        model.addAttribute("applications", applications);
        model.addAttribute("userRole", "company");
        return "cv_filtering/applicants-preview";
    }
    
    @GetMapping("/results")
    public String results(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        model.addAttribute("runs", dashboardService.getAllRuns(companyId));
        model.addAttribute("userRole", "company");
        return "cv_filtering/results";
    }
    
    @GetMapping("/results/{id}/download")
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> downloadRunResults(@PathVariable String id) {
        byte[] pdfBytes = dashboardService.generatePdfForRun(id);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "UniIntern_Results_" + id + ".pdf");
        
        return new org.springframework.http.ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);
    }
    
    @GetMapping("/ranking-detail/{id}")
    public String rankingDetail(@PathVariable String id, Model model) {
        FilterRun run = dashboardService.getRunById(id);
        List<FilterResult> results = dashboardService.getResultsByRunId(id);
        model.addAttribute("run", run);
        model.addAttribute("results", results);
        model.addAttribute("userRole", "company");
        return "cv_filtering/ranking-detail";
    }
    
    @PostMapping("/ranking-detail/{id}/approve")
    public String approveRun(@PathVariable String id) {
        dashboardService.updateRunStatus(id, "approved");
        return "redirect:/company/cv/ranking-detail/" + id;
    }
    
    @PostMapping("/ranking-detail/{id}/reject")
    public String rejectRun(@PathVariable String id) {
        dashboardService.updateRunStatus(id, "rejected");
        return "redirect:/company/cv/ranking-detail/" + id;
    }
    
    @GetMapping("/rules-weights")
    public String rulesAndWeights(Model model) {
        model.addAttribute("userRole", "company");
        return "cv_filtering/rules-weights";
    }
    
    @GetMapping("/logs")
    public String logs(Model model, HttpSession session) {
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        model.addAttribute("logs", filteringLogRepository.findByCompanyIdOrderByRunAtDesc(companyId));
        model.addAttribute("userRole", "company");
        return "cv_filtering/logs";
    }
    
    @PostMapping("/save-weights")
    public String saveWeights(@RequestParam String internshipId,
                             @RequestParam Integer skillsWeight,
                             @RequestParam Integer gpaWeight,
                             @RequestParam Integer experienceWeight,
                             @RequestParam Integer certificatesWeight,
                             @RequestParam(required = false) Double minimumThreshold,
                             @RequestParam(required = false) Integer topN,
                             RedirectAttributes redirectAttributes) {
        try {
            var internship = dashboardService.getInternshipById(internshipId);
            if (internship == null) {
                redirectAttributes.addFlashAttribute("error", "Internship not found.");
                return "redirect:/company/cv/marking-guides";
            }
            
            // Validate weights sum to 100
            int total = (skillsWeight != null ? skillsWeight : 0) + 
                        (gpaWeight != null ? gpaWeight : 0) + 
                        (experienceWeight != null ? experienceWeight : 0) + 
                        (certificatesWeight != null ? certificatesWeight : 0);
                        
            if (total != 100) {
                redirectAttributes.addFlashAttribute("error", "Weights must sum to 100%. Current total: " + total + "%");
                return "redirect:/company/cv/marking-guide-detail/" + internshipId;
            }
            
            internship.setSkillsWeight(skillsWeight);
            internship.setGpaWeight(gpaWeight);
            internship.setExperienceWeight(experienceWeight);
            internship.setCertificatesWeight(certificatesWeight);
            internship.setMinimumThreshold(minimumThreshold);
            if (topN != null) internship.setTopNCandidates(topN);
            
            dashboardService.saveInternship(internship);
            
            redirectAttributes.addFlashAttribute("message", "Marking guide updated successfully!");
            return "redirect:/company/cv/marking-guide-detail/" + internshipId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving weights: " + e.getMessage());
            return "redirect:/company/cv/marking-guides";
        }
    }
}
