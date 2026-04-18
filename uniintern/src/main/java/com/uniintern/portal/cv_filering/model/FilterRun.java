package com.uniintern.portal.cv_filering.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cv_filter_runs")
public class FilterRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long internshipId;
    private String internshipTitle;
    private Long companyId;
    private String company;
    
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private int applicantsProcessed;
    private String status; // pending_approval, approved, rejected, failed
    private int topN;
    private Double minimumThreshold;
    
    public FilterRun() {}
    
    public FilterRun(Long internshipId, String internshipTitle, Long companyId, String company, 
                     LocalDateTime startedAt, LocalDateTime finishedAt, 
                     int applicantsProcessed, String status, int topN, Double minimumThreshold) {
        this.internshipId = internshipId;
        this.internshipTitle = internshipTitle;
        this.companyId = companyId;
        this.company = company;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.applicantsProcessed = applicantsProcessed;
        this.status = status;
        this.topN = topN;
        this.minimumThreshold = minimumThreshold;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getInternshipId() { return internshipId; }
    public void setInternshipId(Long internshipId) { this.internshipId = internshipId; }
    
    public String getInternshipTitle() { return internshipTitle; }
    public void setInternshipTitle(String internshipTitle) { this.internshipTitle = internshipTitle; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
    
    public int getApplicantsProcessed() { return applicantsProcessed; }
    public void setApplicantsProcessed(int applicantsProcessed) { this.applicantsProcessed = applicantsProcessed; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getTopN() { return topN; }
    public void setTopN(int topN) { this.topN = topN; }

    public Double getMinimumThreshold() { return minimumThreshold; }
    public void setMinimumThreshold(Double minimumThreshold) { this.minimumThreshold = minimumThreshold; }
}
