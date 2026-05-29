package com.uniintern.portal.student.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String university;

    @Column(nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String nicNumber;

    @Column(nullable = false)
    private String degreeProgram;

    @Column(nullable = false)
    private String academicYear;

    @Column(nullable = false)
    private Double gpa;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    private String cvFilePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status;

    @Column(nullable = false)
    private Boolean loginAlertsEnabled = false;

    @Column(nullable = false)
    private Boolean twoFactorEnabled = false;

    @Column(nullable = false)
    private Boolean rememberDeviceEnabled = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;

    public Student() {
        this.loginAlertsEnabled = false;
        this.twoFactorEnabled = false;
        this.rememberDeviceEnabled = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = StudentStatus.PENDING_VERIFICATION;
        }
        if (this.loginAlertsEnabled == null) this.loginAlertsEnabled = false;
        if (this.twoFactorEnabled == null) this.twoFactorEnabled = false;
        if (this.rememberDeviceEnabled == null) this.rememberDeviceEnabled = false;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }

    public String getDegreeProgram() {
        return degreeProgram;
    }

    public void setDegreeProgram(String degreeProgram) {
        this.degreeProgram = degreeProgram;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public String getCvFilePath() {
        return cvFilePath;
    }

    public void setCvFilePath(String cvFilePath) {
        this.cvFilePath = cvFilePath;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    @Transient
    public int getExperienceCount() {
        if (experience == null || experience.isEmpty()) return 0;
        return experience.split(",").length;
    }

    @Transient
    public int getExperienceMonths() {
        if (experience == null || experience.isEmpty()) return 0;
        return getExperienceCount() * 6; 
    }

    public Boolean getLoginAlertsEnabled() {
        return loginAlertsEnabled;
    }

    public void setLoginAlertsEnabled(Boolean loginAlertsEnabled) {
        this.loginAlertsEnabled = loginAlertsEnabled;
    }

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public Boolean getRememberDeviceEnabled() {
        return rememberDeviceEnabled;
    }

    public void setRememberDeviceEnabled(Boolean rememberDeviceEnabled) {
        this.rememberDeviceEnabled = rememberDeviceEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}