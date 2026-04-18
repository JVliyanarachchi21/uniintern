package com.uniintern.portal.cv_filering.model;

import jakarta.persistence.*;
import com.uniintern.portal.student.model.Student;

@Entity
@Table(name = "cv_filter_results")
public class FilterResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long runId;
    private Long applicationId;
    
    private String studentName;
    private String university;
    
    private double totalScore;
    private double skillScore;
    private double gpaScore;
    private double experienceScore;
    private double certScore;
    
    private int rankNumber;
    private boolean isTopN;
    
    private int skillsMatched;
    private int totalRequiredSkills;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;
    
    public FilterResult() {}
    
    public FilterResult(Long runId, Long applicationId, String studentName, String university,
                        double totalScore, double skillScore, double gpaScore, double experienceScore,
                        double certScore, int rankNumber, boolean isTopN, int skillsMatched, int totalRequiredSkills) {
        this.runId = runId;
        this.applicationId = applicationId;
        this.studentName = studentName;
        this.university = university;
        this.totalScore = totalScore;
        this.skillScore = skillScore;
        this.gpaScore = gpaScore;
        this.experienceScore = experienceScore;
        this.certScore = certScore;
        this.rankNumber = rankNumber;
        this.isTopN = isTopN;
        this.skillsMatched = skillsMatched;
        this.totalRequiredSkills = totalRequiredSkills;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getRunId() { return runId; }
    public void setRunId(Long runId) { this.runId = runId; }
    
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    
    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }
    
    public double getSkillScore() { return skillScore; }
    public void setSkillScore(double skillScore) { this.skillScore = skillScore; }
    
    public double getGpaScore() { return gpaScore; }
    public void setGpaScore(double gpaScore) { this.gpaScore = gpaScore; }
    
    public double getExperienceScore() { return experienceScore; }
    public void setExperienceScore(double experienceScore) { this.experienceScore = experienceScore; }
    
    public double getCertScore() { return certScore; }
    public void setCertScore(double certScore) { this.certScore = certScore; }
    
    public int getRankNumber() { return rankNumber; }
    public void setRankNumber(int rankNumber) { this.rankNumber = rankNumber; }
    
    public boolean isTopN() { return isTopN; }
    public void setTopN(boolean topN) { isTopN = topN; }
    
    public int getSkillsMatched() { return skillsMatched; }
    public void setSkillsMatched(int skillsMatched) { this.skillsMatched = skillsMatched; }
    
    public int getTotalRequiredSkills() { return totalRequiredSkills; }
    public void setTotalRequiredSkills(int totalRequiredSkills) { this.totalRequiredSkills = totalRequiredSkills; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
}
