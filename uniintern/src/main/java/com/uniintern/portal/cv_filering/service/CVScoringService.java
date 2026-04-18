package com.uniintern.portal.cv_filering.service;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.student.model.Student;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CVScoringService {
    
    /**
     * Calculate skill score based on keyword matching
     * Formula: (matchedSkills / totalRequiredSkills) * skillsWeight
     */
    public double calculateSkillScore(String studentSkills, String requiredSkills, int weight) {
        if (requiredSkills == null || requiredSkills.trim().isEmpty()) {
            return 0.0;
        }
        
        if (studentSkills == null || studentSkills.trim().isEmpty()) {
            return 0.0;
        }
        
        List<String> required = Arrays.stream(requiredSkills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        
        if (required.isEmpty()) {
            return 0.0;
        }
        
        List<String> student = Arrays.stream(studentSkills.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        
        long matchedCount = required.stream()
                .filter(skill -> student.contains(skill.toLowerCase()))
                .count();
        
        return ((double) matchedCount / required.size()) * weight;
    }
    
    /**
     * Calculate GPA score
     * Formula: (studentGpa / 4.0) * gpaWeight
     */
    public double calculateGpaScore(Double studentGpa, int weight) {
        if (studentGpa == null) {
            return 0.0;
        }
        
        return (studentGpa / 4.0) * weight;
    }
    
    /**
     * Calculate experience score based on number of experience entries
     * Formula: min(experienceCount * (weight / 5.0), weight)
     * Caps at the full weight value
     */
    public double calculateExperienceScore(String studentExperience, int weight) {
        if (studentExperience == null || studentExperience.trim().isEmpty()) {
            return 0.0;
        }
        
        // Count experience entries (split by newline or semicolon)
        long experienceCount = Arrays.stream(studentExperience.split("[;\n]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .count();
        
        if (experienceCount == 0) {
            return 0.0;
        }
        
        // Each entry gives weight/5 points, capped at full weight
        double score = experienceCount * (weight / 5.0);
        return Math.min(score, weight);
    }
    
    /**
     * Calculate certificate score based on number of certifications
     * Formula: min(certCount * (weight / 5.0), weight)
     * Caps at the full weight value
     */
    public double calculateCertificateScore(String studentCerts, int weight) {
        if (studentCerts == null || studentCerts.trim().isEmpty()) {
            return 0.0;
        }
        
        // Count certifications (split by comma or newline)
        long certCount = Arrays.stream(studentCerts.split("[,\n]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .count();
        
        if (certCount == 0) {
            return 0.0;
        }
        
        // Each cert gives weight/5 points, capped at full weight
        double score = certCount * (weight / 5.0);
        return Math.min(score, weight);
    }
    
    /**
     * Calculate total weighted score for a student against an internship
     * Formula: (Skill% × Ws) + (GPA% × Wg) + (Cert% × Wc) + (Exp% × We)
     */
    public double calculateTotalScore(Student student, Internship internship) {
        int skillsWeight = internship.getSkillsWeight() != null ? internship.getSkillsWeight() : 0;
        int gpaWeight = internship.getGpaWeight() != null ? internship.getGpaWeight() : 0;
        int experienceWeight = internship.getExperienceWeight() != null ? internship.getExperienceWeight() : 0;
        int certificateWeight = internship.getCertificatesWeight() != null ? internship.getCertificatesWeight() : 0;
        
        double skillScore = calculateSkillScore(
                student.getSkills(), 
                internship.getRequiredSkills(), 
                skillsWeight
        );
        
        double gpaScore = calculateGpaScore(student.getGpa(), gpaWeight);
        
        double experienceScore = calculateExperienceScore(
                student.getExperience(), 
                experienceWeight
        );
        
        double certificateScore = calculateCertificateScore(
                student.getCertifications(), 
                certificateWeight
        );
        
        return skillScore + gpaScore + experienceScore + certificateScore;
    }
    
    /**
     * Validate that all weights sum to 100%
     */
    public boolean validateWeights(Internship internship) {
        if (internship.getSkillsWeight() == null ||
            internship.getGpaWeight() == null ||
            internship.getExperienceWeight() == null ||
            internship.getCertificatesWeight() == null) {
            return false;
        }
        
        int total = internship.getSkillsWeight() + 
                    internship.getGpaWeight() + 
                    internship.getExperienceWeight() + 
                    internship.getCertificatesWeight();
        
        return total == 100;
    }
    
    /**
     * Get the total weight sum
     */
    public int getTotalWeight(Internship internship) {
        int skillsWeight = internship.getSkillsWeight() != null ? internship.getSkillsWeight() : 0;
        int gpaWeight = internship.getGpaWeight() != null ? internship.getGpaWeight() : 0;
        int experienceWeight = internship.getExperienceWeight() != null ? internship.getExperienceWeight() : 0;
        int certificateWeight = internship.getCertificatesWeight() != null ? internship.getCertificatesWeight() : 0;
        
        return skillsWeight + gpaWeight + experienceWeight + certificateWeight;
    }
}
