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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    
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
    
    public long getReadyInternshipsCount() {
        return internshipRepository.findAll().stream()
                .filter(i -> "closed".equals(i.getStatus()) && i.getSkillsWeight() != null)
                .count();
    }
    
    public long getMissingGuidesCount() {
        return internshipRepository.findAll().stream()
                .filter(i -> i.getSkillsWeight() == null)
                .count();
    }
    
    public long getAwaitingDeadlineCount() {
        return internshipRepository.findAll().stream()
                .filter(i -> "open".equals(i.getStatus()))
                .count();
    }
    
    public long getCompletedRunsCount() {
        return filterRunRepository.findAll().stream()
                .filter(r -> "approved".equals(r.getStatus()) || "completed".equals(r.getStatus()))
                .count();
    }
    
    public long getPendingApprovalsCount() {
        return filterRunRepository.findAll().stream()
                .filter(r -> "pending_approval".equals(r.getStatus()))
                .count();
    }
    
    public List<FilterRun> getRecentRuns(int limit) {
        return filterRunRepository.findAllByOrderByFinishedAtDesc().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    public List<Internship> getAllInternships() {
        return internshipRepository.findAll();
    }

    public List<Internship> getAvailableInternships() {
        return internshipRepository.findAll().stream()
                .filter(i -> i.getSkillsWeight() != null)
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
            return applicationRepository.findByInternshipId(longId);
        } catch(Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<FilterRun> getAllRuns() {
        return filterRunRepository.findAllByOrderByFinishedAtDesc();
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
        Internship internship = getInternshipById(internshipIdStr);
        List<StudentApplication> apps = getApplicationsByInternshipId(internshipIdStr);
        
        if (internship == null || internship.getSkillsWeight() == null) return null;
        
        LocalDateTime startedAt = LocalDateTime.now();
        
        // We assume MarkingGuide properties are built in Internship
        List<String> requiredSkills = internship.getRequiredSkills() != null ? 
            Arrays.asList(internship.getRequiredSkills().split(",")) : new ArrayList<>();
            
        int topN = internship.getTopNCandidates() != null ? internship.getTopNCandidates() : 10;
        
        FilterRun newRun = new FilterRun(
            internship.getId(),
            internship.getTitle(),
            "Company " + internship.getCompanyId(), // placeholder
            startedAt,
            LocalDateTime.now(), // to be updated
            apps.size(),
            "pending_approval",
            topN
        );
        newRun = filterRunRepository.save(newRun);
        
        List<FilterResult> runResults = new ArrayList<>();
        int rank = 1;

        for (StudentApplication app : apps) {
            Student student = studentRepository.findById(app.getStudentId()).orElse(null);
            if (student == null) continue;
            
            // Calculate scores
            int skillsMatched = 0;
            for (String skill : requiredSkills) {
                if (student.getSkills() != null && student.getSkills().contains(skill.trim())) {
                    skillsMatched++;
                }
            }
            double skillScore = requiredSkills.isEmpty() ? 0 : 
                ((double) skillsMatched / requiredSkills.size()) * internship.getSkillsWeight();
            
            double studentGpa = student.getGpa() != null ? student.getGpa() : 0.0;
            int gpaWeight = internship.getGpaWeight() != null ? internship.getGpaWeight() : 0;
            double gpaScore = (studentGpa / 4.0) * gpaWeight;
            
            double expScore = 0.0; // Needs parsing experience
            int certWeight = internship.getCertificatesWeight() != null ? internship.getCertificatesWeight() : 0;
            double certScore = (student.getCertifications() == null || student.getCertifications().isEmpty()) ? 0 : certWeight;
            
            double totalScore = skillScore + gpaScore + expScore + certScore;
            
            // Update StudentApplication Score
            app.setScore(totalScore);
            applicationRepository.save(app);
            
            FilterResult result = new FilterResult(
                newRun.getId(),
                app.getId(),
                student.getFullName(),
                student.getUniversity(),
                totalScore,
                skillScore,
                gpaScore,
                expScore,
                certScore,
                0, // rank updated later
                false, // topN updated later
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
        filterRunRepository.save(newRun);
        
        LogEntry log = new LogEntry(
            newRun.getId(),
            internship.getTitle(),
            startedAt,
            newRun.getFinishedAt(),
            apps.size(),
            "success",
            "Completed successfully. Awaiting admin approval."
        );
        logEntryRepository.save(log);
        
        return newRun;
    }
}
