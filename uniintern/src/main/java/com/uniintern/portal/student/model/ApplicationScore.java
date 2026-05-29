package com.uniintern.portal.student.model;

import jakarta.persistence.*;

@Entity
@Table(name = "application_scores")
public class ApplicationScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long applicationId;

    @Column(nullable = false)
    private String category;

    private Integer weight;

    @Column(columnDefinition = "TEXT")
    private String criteria;

    @Column(columnDefinition = "TEXT")
    private String studentData;

    private Integer scoreValue;

    public ApplicationScore() {
    }

    public ApplicationScore(Long applicationId, String category, Integer weight, String criteria, String studentData, Integer scoreValue) {
        this.applicationId = applicationId;
        this.category = category;
        this.weight = weight;
        this.criteria = criteria;
        this.studentData = studentData;
        this.scoreValue = scoreValue;
    }

    public Long getId() {
        return id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getStudentData() {
        return studentData;
    }

    public void setStudentData(String studentData) {
        this.studentData = studentData;
    }

    public Integer getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(Integer scoreValue) {
        this.scoreValue = scoreValue;
    }
}
