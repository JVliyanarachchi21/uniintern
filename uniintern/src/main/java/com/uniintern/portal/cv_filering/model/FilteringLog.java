package com.uniintern.portal.cv_filering.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "filtering_logs")
public class FilteringLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String runBy;
    private String userRole; // "admin" or "company"
    private Long companyId;
    private Long internshipId;
    private String internshipTitle;
    private LocalDateTime runAt;
    private int totalCandidates;
    private String status; // "success", "failed"
    
    public FilteringLog() {}
    
    public FilteringLog(String runBy, String userRole, Long companyId, Long internshipId, String internshipTitle,
                        LocalDateTime runAt, int totalCandidates, String status) {
        this.runBy = runBy;
        this.userRole = userRole;
        this.companyId = companyId;
        this.internshipId = internshipId;
        this.internshipTitle = internshipTitle;
        this.runAt = runAt;
        this.totalCandidates = totalCandidates;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRunBy() { return runBy; }
    public void setRunBy(String runBy) { this.runBy = runBy; }
    
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public Long getInternshipId() { return internshipId; }
    public void setInternshipId(Long internshipId) { this.internshipId = internshipId; }
    
    public String getInternshipTitle() { return internshipTitle; }
    public void setInternshipTitle(String internshipTitle) { this.internshipTitle = internshipTitle; }
    
    public LocalDateTime getRunAt() { return runAt; }
    public void setRunAt(LocalDateTime runAt) { this.runAt = runAt; }
    
    public int getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(int totalCandidates) { this.totalCandidates = totalCandidates; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
