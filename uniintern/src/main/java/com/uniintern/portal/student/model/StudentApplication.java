package com.uniintern.portal.student.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_applications")
public class StudentApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long internshipId;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private Double score;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    public StudentApplication() {
    }

    @Transient
    public int getCompleteness() {
        if (student == null) return 50; // Default if not loaded
        int filledFields = 0;
        int totalFields = 8;
        
        if (student.getFullName() != null && !student.getFullName().isEmpty()) filledFields++;
        if (student.getEmail() != null && !student.getEmail().isEmpty()) filledFields++;
        if (student.getUniversity() != null && !student.getUniversity().isEmpty()) filledFields++;
        if (student.getGpa() != null && student.getGpa() > 0) filledFields++;
        if (student.getSkills() != null && !student.getSkills().isEmpty()) filledFields++;
        if (student.getExperience() != null && !student.getExperience().isEmpty()) filledFields++;
        if (student.getCertifications() != null && !student.getCertifications().isEmpty()) filledFields++;
        if (student.getCvFilePath() != null && !student.getCvFilePath().isEmpty()) filledFields++;
        
        return (int) ((filledFields / (double) totalFields) * 100);
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ApplicationStatus.APPLIED;
        }
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(Long internshipId) {
        this.internshipId = internshipId;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}