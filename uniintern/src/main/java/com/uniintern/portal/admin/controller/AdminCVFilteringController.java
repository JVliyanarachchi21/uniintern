package com.uniintern.portal.admin.controller;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.repository.InternshipRepository;
import com.uniintern.portal.cv_filering.model.FilterRun;
import com.uniintern.portal.cv_filering.model.FilteringLog;
import com.uniintern.portal.cv_filering.repo.FilterRunRepository;
import com.uniintern.portal.cv_filering.repo.FilteringLogRepository;
import com.uniintern.portal.cv_filering.service.CVScoringService;
import com.uniintern.portal.cv_filering.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/cv")
public class AdminCVFilteringController {
    
    private final DashboardService dashboardService;
    private final CVScoringService cvScoringService;
    private final InternshipRepository internshipRepository;
    private final FilterRunRepository filterRunRepository;
    private final FilteringLogRepository filteringLogRepository;
    
    public AdminCVFilteringController(DashboardService dashboardService,
                                      CVScoringService cvScoringService,
                                      InternshipRepository internshipRepository,
                                      FilterRunRepository filterRunRepository,
                                      FilteringLogRepository filteringLogRepository) {
        this.dashboardService = dashboardService;
        this.cvScoringService = cvScoringService;
        this.internshipRepository = internshipRepository;
        this.filterRunRepository = filterRunRepository;
        this.filteringLogRepository = filteringLogRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("readyInternshipsCount", dashboardService.getReadyInternshipsCount());
        model.addAttribute("missingGuidesCount", dashboardService.getMissingGuidesCount());
        model.addAttribute("awaitingDeadlineCount", dashboardService.getAwaitingDeadlineCount());
        model.addAttribute("completedRunsCount", dashboardService.getCompletedRunsCount());
        model.addAttribute("pendingApprovalsCount", dashboardService.getPendingApprovalsCount());
        model.addAttribute("recentRuns", dashboardService.getRecentRuns(5));
        model.addAttribute("userRole", "admin");
        return "cv_filtering/dashboard";
    }
    
    @GetMapping("/marking-guides")
    public String markingGuides(Model model) {
        model.addAttribute("internships", internshipRepository.findAll());
        model.addAttribute("userRole", "admin");
        return "cv_filtering/marking-guides";
    }
    
    @GetMapping("/marking-guide-detail/{id}")
    public String markingGuideDetail(@PathVariable String id, Model model) {
        Internship guide = dashboardService.getInternshipById(id);
        model.addAttribute("guide", guide);
        model.addAttribute("userRole", "admin");
        model.addAttribute("totalWeight", cvScoringService.getTotalWeight(guide));
        model.addAttribute("weightsValid", cvScoringService.validateWeights(guide));
        return "cv_filtering/marking-guide-detail";
    }
    
    @PostMapping("/marking-guide-detail/{id}/update")
    public String updateMarkingGuide(@PathVariable String id,
                                     @RequestParam Integer skillsWeight,
                                     @RequestParam Integer gpaWeight,
                                     @RequestParam Integer experienceWeight,
                                     @RequestParam Integer certificatesWeight,
                                     @RequestParam(required = false) Double minimumThreshold,
                                     RedirectAttributes redirectAttributes) {
        try {
            Long internshipId = Long.parseLong(id);
            Internship internship = internshipRepository.findById(internshipId).orElse(null);
            
            if (internship == null) {
                redirectAttributes.addFlashAttribute("error", "Internship not found.");
                return "redirect:/admin/cv/marking-guides";
            }
            
            // Validate weights sum to 100
            int total = skillsWeight + gpaWeight + experienceWeight + certificatesWeight;
            if (total != 100) {
                redirectAttributes.addFlashAttribute("error", "Weights must sum to 100%. Current total: " + total + "%");
                return "redirect:/admin/cv/marking-guide-detail/" + id;
            }
            
            internship.setSkillsWeight(skillsWeight);
            internship.setGpaWeight(gpaWeight);
            internship.setExperienceWeight(experienceWeight);
            internship.setCertificatesWeight(certificatesWeight);
            internship.setMinimumThreshold(minimumThreshold);
            
            internshipRepository.save(internship);
            redirectAttributes.addFlashAttribute("message", "Marking guide updated successfully!");
            return "redirect:/admin/cv/marking-guide-detail/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update marking guide: " + e.getMessage());
            return "redirect:/admin/cv/marking-guide-detail/" + id;
        }
    }
    
    @GetMapping("/run-filtering")
    public String runFiltering(Model model) {
        // Show ALL approved internships for admin, not just those with weights set
        model.addAttribute("internships", internshipRepository.findByStatus("APPROVED"));
        model.addAttribute("userRole", "admin");
        return "cv_filtering/run-filtering";
    }
    
    @PostMapping("/run-filtering")
    public String runFilteringPost(@RequestParam String internshipId, 
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Run filtering as admin
            FilterRun run = dashboardService.runFiltering(internshipId);
            
            if (run != null) {
                // Log the filtering run
                FilteringLog log = new FilteringLog(
                    "admin",
                    "admin",
                    run.getInternshipId(),
                    run.getInternshipTitle(),
                    LocalDateTime.now(),
                    run.getApplicantsProcessed(),
                    "success"
                );
                filteringLogRepository.save(log);
                
                redirectAttributes.addFlashAttribute("message", "Filtering completed successfully!");
                return "redirect:/admin/cv/results";
            }
            
            redirectAttributes.addFlashAttribute("error", "Failed to run filtering.");
            return "redirect:/admin/cv/run-filtering";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/admin/cv/run-filtering";
        }
    }
    
    @GetMapping("/applicants-preview")
    public String applicantsPreview(@RequestParam String internshipId, Model model) {
        Internship internship = dashboardService.getInternshipById(internshipId);
        model.addAttribute("internship", internship);
        model.addAttribute("applications", dashboardService.getApplicationsByInternshipId(internshipId));
        model.addAttribute("userRole", "admin");
        return "cv_filtering/applicants-preview";
    }
    
    @GetMapping("/results")
    public String results(Model model) {
        model.addAttribute("runs", dashboardService.getAllRuns());
        model.addAttribute("userRole", "admin");
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
        model.addAttribute("run", run);
        model.addAttribute("results", dashboardService.getResultsByRunId(id));
        model.addAttribute("userRole", "admin");
        return "cv_filtering/ranking-detail";
    }
    
    @PostMapping("/ranking-detail/{id}/approve")
    public String approveRun(@PathVariable String id) {
        dashboardService.updateRunStatus(id, "approved");
        return "redirect:/admin/cv/ranking-detail/" + id;
    }
    
    @PostMapping("/ranking-detail/{id}/reject")
    public String rejectRun(@PathVariable String id) {
        dashboardService.updateRunStatus(id, "rejected");
        return "redirect:/admin/cv/ranking-detail/" + id;
    }
    
    @GetMapping("/logs")
    public String logs(Model model) {
        model.addAttribute("logs", filteringLogRepository.findAllByOrderByRunAtDesc());
        model.addAttribute("userRole", "admin");
        return "cv_filtering/logs";
    }
    
    @GetMapping("/settings")
    public String settings(org.springframework.ui.Model model) {
        model.addAttribute("userRole", "admin");
        return "cv_filtering/settings";
    }
}
