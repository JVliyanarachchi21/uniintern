package com.uniintern.portal.cv_filering.controller;

import com.uniintern.portal.cv_filering.model.*;
import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.student.model.StudentApplication;
import com.uniintern.portal.cv_filering.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/company/cv")
public class PageController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/company/cv/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("readyInternshipsCount", dashboardService.getReadyInternshipsCount());
        model.addAttribute("missingGuidesCount", dashboardService.getMissingGuidesCount());
        model.addAttribute("awaitingDeadlineCount", dashboardService.getAwaitingDeadlineCount());
        model.addAttribute("completedRunsCount", dashboardService.getCompletedRunsCount());
        model.addAttribute("pendingApprovalsCount", dashboardService.getPendingApprovalsCount());
        model.addAttribute("recentRuns", dashboardService.getRecentRuns(5));
        return "cv_filtering/dashboard";
    }
    
    @GetMapping("/marking-guides")
    public String markingGuides(Model model) {
        model.addAttribute("internships", dashboardService.getAllInternships());
        return "cv_filtering/marking-guides";
    }
    
    @GetMapping("/marking-guide-detail/{id}")
    public String markingGuideDetail(@PathVariable String id, Model model) {
        Internship guide = dashboardService.getInternshipById(id);
        model.addAttribute("guide", guide);
        return "cv_filtering/marking-guide-detail";
    }
    
    @GetMapping("/run-filtering")
    public String runFiltering(Model model) {
        model.addAttribute("availableInternships", dashboardService.getAvailableInternships());
        return "cv_filtering/run-filtering";
    }
    
    @PostMapping("/run-filtering")
    public String runFilteringPost(@RequestParam String internshipId, RedirectAttributes redirectAttributes) {
        FilterRun run = dashboardService.runFiltering(internshipId);
        if (run != null) {
            redirectAttributes.addFlashAttribute("message", "Filtering completed successfully!");
            return "redirect:/company/cv/results";
        }
        redirectAttributes.addFlashAttribute("error", "Failed to run filtering.");
        return "redirect:/company/cv/run-filtering";
    }
    
    @GetMapping("/applicants-preview")
    public String applicantsPreview(@RequestParam String internshipId, Model model) {
        Internship internship = dashboardService.getInternshipById(internshipId);
        List<StudentApplication> applications = dashboardService.getApplicationsByInternshipId(internshipId);
        model.addAttribute("internship", internship);
        model.addAttribute("applications", applications);
        return "cv_filtering/applicants-preview";
    }
    
    @GetMapping("/results")
    public String results(Model model) {
        model.addAttribute("runs", dashboardService.getAllRuns());
        return "cv_filtering/results";
    }
    
    @GetMapping("/ranking-detail/{id}")
    public String rankingDetail(@PathVariable String id, Model model) {
        FilterRun run = dashboardService.getRunById(id);
        List<FilterResult> results = dashboardService.getResultsByRunId(id);
        model.addAttribute("run", run);
        model.addAttribute("results", results);
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
    public String rulesAndWeights() {
        return "cv_filtering/rules-weights";
    }
    
    @GetMapping("/logs")
    public String logs(Model model) {
        model.addAttribute("logs", dashboardService.getAllLogs());
        return "cv_filtering/logs";
    }
    
    @GetMapping("/settings")
    public String settings() {
        return "cv_filtering/settings";
    }
}
