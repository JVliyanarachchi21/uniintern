package com.uniintern.portal.cv_filering.service;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.repository.InternshipRepository;
import com.uniintern.portal.student.model.StudentApplication;
import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.repository.StudentApplicationRepository;
import com.uniintern.portal.student.repository.StudentRepository;
import com.uniintern.portal.cv_filering.model.FilterResult;
import com.uniintern.portal.cv_filering.model.FilterRun;
import com.uniintern.portal.cv_filering.model.LogEntry;
import com.uniintern.portal.cv_filering.repo.FilterResultRepository;
import com.uniintern.portal.cv_filering.repo.FilterRunRepository;
import com.uniintern.portal.cv_filering.repo.LogEntryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class DashboardService {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    
    @Autowired
    private InternshipRepository internshipRepository;
    
    @Autowired
    private StudentApplicationRepository applicationRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private FilterRunRepository filterRunRepository;
    
    @Autowired
    private FilterResultRepository filterResultRepository;
    
    @Autowired
    private LogEntryRepository logEntryRepository;
    
    @Autowired
    private CVScoringService cvScoringService;

    @Autowired
    private TemplateEngine templateEngine;

    @jakarta.annotation.PostConstruct
    @Transactional
    public void migratePendingRuns() {
        logger.info("Migrating any legacy 'pending_approval' filter runs to 'completed'...");
        List<FilterRun> pendingRuns = filterRunRepository.findAll().stream()
                .filter(r -> "pending_approval".equals(r.getStatus()))
                .collect(Collectors.toList());
        
        for (FilterRun run : pendingRuns) {
            run.setStatus("completed");
            filterRunRepository.save(run);
        }
        if (!pendingRuns.isEmpty()) {
            logger.info("Successfully migrated {} runs.", pendingRuns.size());
        }
    }
    
    public long getReadyInternshipsCount(Long companyId) {
        return internshipRepository.findAll().stream()
                .filter(i -> (companyId == null || i.getCompanyId().equals(companyId)) && 
                             "closed".equals(i.getStatus()) && i.getSkillsWeight() != null)
                .count();
    }
    
    public long getMissingGuidesCount(Long companyId) {
        return internshipRepository.findAll().stream()
                .filter(i -> (companyId == null || i.getCompanyId().equals(companyId)) && 
                             i.getSkillsWeight() == null)
                .count();
    }
    
    public long getAwaitingDeadlineCount(Long companyId) {
        return internshipRepository.findAll().stream()
                .filter(i -> (companyId == null || i.getCompanyId().equals(companyId)) && 
                             "open".equals(i.getStatus()))
                .count();
    }
    
    public long getCompletedRunsCount(Long companyId) {
        return filterRunRepository.findAll().stream()
                .filter(r -> (companyId == null || (r.getCompanyId() != null && r.getCompanyId().equals(companyId))) && 
                             ("approved".equals(r.getStatus()) || "completed".equals(r.getStatus())))
                .count();
    }
    
    public long getPendingApprovalsCount(Long companyId) {
        return filterRunRepository.findAll().stream()
                .filter(r -> (companyId == null || (r.getCompanyId() != null && r.getCompanyId().equals(companyId))) && 
                             "pending_approval".equals(r.getStatus()))
                .count();
    }
    
    public List<FilterRun> getRecentRuns(int limit, Long companyId) {
        List<FilterRun> runs = (companyId == null) ? 
            filterRunRepository.findAllByOrderByFinishedAtDesc() : 
            filterRunRepository.findByCompanyIdOrderByFinishedAtDesc(companyId);
            
        return runs.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    public List<Internship> getAllInternships() {
        return internshipRepository.findAll();
    }
    
    public List<Internship> getInternshipsByCompanyId(Long companyId) {
        if (companyId == null) {
            return new ArrayList<>();
        }
        return internshipRepository.findByCompanyId(companyId);
    }

    public List<Internship> getAvailableInternships() {
        return internshipRepository.findBySkillsWeightIsNotNull();
    }
    
    public List<Internship> getAvailableInternshipsByCompanyId(Long companyId) {
        if (companyId == null) {
            return new ArrayList<>();
        }
        return internshipRepository.findByCompanyIdAndSkillsWeightIsNotNull(companyId);
    }
    
    /**
     * Get ALL approved internships for a company (for run filtering dropdown)
     * Shows internships even if weights are not set yet
     */
    public List<Internship> getAllApprovedInternshipsByCompanyId(Long companyId) {
        if (companyId == null) {
            return new ArrayList<>();
        }
        // Fetch all and filter to handle mixed casing (APPROVED/approved)
        return internshipRepository.findByCompanyId(companyId).stream()
                .filter(i -> "APPROVED".equalsIgnoreCase(i.getStatus()) || "OPEN".equalsIgnoreCase(i.getStatus()))
                .collect(Collectors.toList());
    }

    // Return the Internship object since MarkingGuide is merged into it
    public Internship getGuideByInternshipId(String internshipId) {
        return getInternshipById(internshipId);
    }
    
    public Internship getInternshipById(String id) {
        try {
            Long longId = Long.parseLong(id);
            return internshipRepository.findById(longId).orElse(null);
        } catch(Exception e) {
            return null;
        }
    }
    
    public List<StudentApplication> getApplicationsByInternshipId(String internshipId) {
         try {
            Long longId = Long.parseLong(internshipId);
            List<StudentApplication> apps = applicationRepository.findByInternshipId(longId);
            logger.info("Retrieved {} applications for internship ID: {}", apps.size(), internshipId);
            return apps;
        } catch(Exception e) {
            logger.error("Error retrieving applications for internship ID: {}: {}", internshipId, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<FilterRun> getAllRuns(Long companyId) {
        if (companyId == null) {
            return filterRunRepository.findAllByOrderByFinishedAtDesc();
        }
        return filterRunRepository.findByCompanyIdOrderByFinishedAtDesc(companyId);
    }
    
    public FilterRun getRunById(String id) {
        try {
            Long longId = Long.parseLong(id);
            return filterRunRepository.findById(longId).orElse(null);
        } catch(Exception e) {
            return null;
        }
    }
    
    public List<FilterResult> getResultsByRunId(String runId) {
         try {
            Long longId = Long.parseLong(runId);
            return filterResultRepository.findByRunIdOrderByRankNumberAsc(longId);
        } catch(Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<LogEntry> getAllLogs() {
        return logEntryRepository.findAll(); // Could add order by
    }
    
    @Transactional
    public void updateRunStatus(String runIdStr, String status) {
        FilterRun run = getRunById(runIdStr);
        if (run != null) {
            run.setStatus(status);
            filterRunRepository.save(run);
            
            LogEntry log = new LogEntry(
                run.getId(),
                run.getInternshipTitle(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                run.getApplicantsProcessed(),
                "success",
                "Admin " + status + " the results."
            );
            logEntryRepository.save(log);
        }
    }
    
    @Transactional
    public FilterRun runFiltering(String internshipIdStr) {
        return runFilteringForCompany(internshipIdStr, null, "admin");
    }
    
    @Transactional
    public FilterRun runFilteringForCompany(String internshipIdStr, Long companyId, String companyName) {
        Internship internship = getInternshipById(internshipIdStr);
        List<StudentApplication> apps = getApplicationsByInternshipId(internshipIdStr);
        
        if (internship == null || internship.getSkillsWeight() == null) {
            return null;
        }
        
        // For companies, verify ownership
        if (companyId != null && !internship.getCompanyId().equals(companyId)) {
            return null;
        }
        
        LocalDateTime startedAt = LocalDateTime.now();
        
        List<String> requiredSkills = internship.getRequiredSkills() != null ? 
            Arrays.asList(internship.getRequiredSkills().split(",")) : new ArrayList<>();
            
        int topN = internship.getTopNCandidates() != null ? internship.getTopNCandidates() : 10;
        Double minimumThreshold = internship.getMinimumThreshold();
        
        FilterRun newRun = new FilterRun(
            internship.getId(),
            internship.getTitle(),
            companyId,
            (companyName != null) ? companyName : ("Company " + internship.getCompanyId()),
            startedAt,
            null,
            0,
            "completed",
            topN,
            internship.getMinimumThreshold()
        );
        filterRunRepository.save(newRun);
        
        List<FilterResult> runResults = new ArrayList<>();

        for (StudentApplication app : apps) {
            Student student = studentRepository.findById(app.getStudentId()).orElse(null);
            if (student == null) continue;
            
            // Use CVScoringService to calculate scores
            double totalScore = cvScoringService.calculateTotalScore(student, internship);
            
            // Calculate individual scores for display
            int skillsWeight = internship.getSkillsWeight() != null ? internship.getSkillsWeight() : 0;
            int gpaWeight = internship.getGpaWeight() != null ? internship.getGpaWeight() : 0;
            int experienceWeight = internship.getExperienceWeight() != null ? internship.getExperienceWeight() : 0;
            int certificatesWeight = internship.getCertificatesWeight() != null ? internship.getCertificatesWeight() : 0;
            
            double skillScore = cvScoringService.calculateSkillScore(
                student.getSkills(), internship.getRequiredSkills(), skillsWeight);
            double gpaScore = cvScoringService.calculateGpaScore(student.getGpa(), gpaWeight);
            double experienceScore = cvScoringService.calculateExperienceScore(
                student.getExperience(), experienceWeight);
            double certScore = cvScoringService.calculateCertificateScore(
                student.getCertifications(), certificatesWeight);
            
            // Apply minimum threshold filter
            if (minimumThreshold != null && totalScore < minimumThreshold) {
                continue; // Skip students below threshold
            }
            
            // Update StudentApplication Score
            app.setScore(totalScore);
            applicationRepository.save(app);
            
            int skillsMatched = 0;
            if (student.getSkills() != null && internship.getRequiredSkills() != null) {
                for (String skill : requiredSkills) {
                    if (student.getSkills().toLowerCase().contains(skill.trim().toLowerCase())) {
                        skillsMatched++;
                    }
                }
            }
            
            FilterResult result = new FilterResult(
                newRun.getId(),
                app.getId(),
                student.getFullName(),
                student.getUniversity(),
                totalScore,
                skillScore,
                gpaScore,
                experienceScore,
                certScore,
                0,
                false,
                skillsMatched,
                requiredSkills.size()
            );
            runResults.add(result);
        }
        
        // Sort by score
        runResults.sort((a, b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));
        
        // Apply ranks
        for (int i = 0; i < runResults.size(); i++) {
            FilterResult fr = runResults.get(i);
            fr.setRankNumber(i + 1);
            fr.setTopN(i < topN);
            filterResultRepository.save(fr);
        }
        
        newRun.setFinishedAt(LocalDateTime.now());
        newRun.setApplicantsProcessed(runResults.size());
        filterRunRepository.save(newRun);
        
        String logMessage = "Analysis completed successfully for " + internship.getTitle();
        
        LogEntry log = new LogEntry(
            newRun.getId(),
            internship.getTitle(),
            startedAt,
            newRun.getFinishedAt(),
            runResults.size(),
            "success",
            logMessage
        );
        logEntryRepository.save(log);
        
        return newRun;
    }

    @Transactional
    public void saveInternship(Internship internship) {
        internshipRepository.save(internship);
    }

    public String generateCsvForRun(String runId) {
        List<FilterResult> results = getResultsByRunId(runId);
        
        StringBuilder csv = new StringBuilder();
        csv.append("Rank,Candidate Name,University,Total Score,Skill Score,GPA Score,Experience Score,Certificate Score\n");
        
        for (FilterResult res : results) {
            csv.append(String.format("%d,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f\n",
                res.getRankNumber(),
                res.getStudentName().replace(",", " "),
                res.getUniversity().replace(",", " "),
                res.getTotalScore(),
                res.getSkillScore(),
                res.getGpaScore(),
                res.getExperienceScore(),
                res.getCertScore()
            ));
        }
        return csv.toString();
    }

    public byte[] generatePdfForRun(String runId) {
        List<FilterResult> results = getResultsByRunId(runId);
        FilterRun run = getRunById(runId);
        
        Context context = new Context();
        context.setVariable("results", results);
        context.setVariable("run", run);
        
        String html = templateEngine.process("cv_filtering/pdf-report", context);
        
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
}
