package com.uniintern.portal.cv_filering.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cv_filter_logs")
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long runId;
    private String internshipTitle;
    
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private int applicantsProcessed;
    private String status; // success, failed
    private String message;
    
    public LogEntry() {}
    
    public LogEntry(Long runId, String internshipTitle, LocalDateTime startedAt, 
                    LocalDateTime finishedAt, int applicantsProcessed, String status, String message) {
        this.runId = runId;
        this.internshipTitle = internshipTitle;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.applicantsProcessed = applicantsProcessed;
        this.status = status;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getRunId() { return runId; }
    public void setRunId(Long runId) { this.runId = runId; }
    
    public String getInternshipTitle() { return internshipTitle; }
    public void setInternshipTitle(String internshipTitle) { this.internshipTitle = internshipTitle; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
    
    public int getApplicantsProcessed() { return applicantsProcessed; }
    public void setApplicantsProcessed(int applicantsProcessed) { this.applicantsProcessed = applicantsProcessed; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
