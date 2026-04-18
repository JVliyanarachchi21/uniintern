package com.uniintern.portal.company.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "internships")
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String requiredSkills;

    private Double minGpa;
    private Double maxGpa;

    private String location;

    private String duration;

    private LocalDate deadline;

    private String status;

    private LocalDateTime createdAt;

    private Integer skillsWeight;
    private Integer gpaWeight;
    private Integer experienceWeight;
    private Integer certificatesWeight;
    private Integer topNCandidates;
    private Double minimumThreshold;

    public Internship() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public Double getMinGpa() {
        return minGpa;
    }

    public void setMinGpa(Double minGpa) {
        this.minGpa = minGpa;
    }

    public Double getMaxGpa() {
        return maxGpa;
    }

    public void setMaxGpa(Double maxGpa) {
        this.maxGpa = maxGpa;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getSkillsWeight() {
        return skillsWeight;
    }

    public void setSkillsWeight(Integer skillsWeight) {
        this.skillsWeight = skillsWeight;
    }

    public Integer getGpaWeight() {
        return gpaWeight;
    }

    public void setGpaWeight(Integer gpaWeight) {
        this.gpaWeight = gpaWeight;
    }

    public Integer getExperienceWeight() {
        return experienceWeight;
    }

    public void setExperienceWeight(Integer experienceWeight) {
        this.experienceWeight = experienceWeight;
    }

    public Integer getCertificatesWeight() {
        return certificatesWeight;
    }

    public void setCertificatesWeight(Integer certificatesWeight) {
        this.certificatesWeight = certificatesWeight;
    }

    public Integer getTopNCandidates() {
        return topNCandidates;
    }

    public void setTopNCandidates(Integer topNCandidates) {
        this.topNCandidates = topNCandidates;
    }

    public Double getMinimumThreshold() {
        return minimumThreshold;
    }

    public void setMinimumThreshold(Double minimumThreshold) {
        this.minimumThreshold = minimumThreshold;
    }
}