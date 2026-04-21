package com.uniintern.portal.admin;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.student.model.Student;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class AdminMatchingService {

    public double calculateMatchScore(Student student, Internship internship) {
        double score = 0.0;

        // 1. Skill Matching (60%)
        double skillScore = calculateSkillScore(student.getSkills(), internship.getRequiredSkills());
        score += skillScore * 0.60;

        // 2. GPA Matching (40%)
        // Assuming max GPA is 4.0. Normalize student GPA.
        double normalizedGpa = (student.getGpa() / 4.0) * 100.0;
        score += normalizedGpa * 0.40;

        return Math.min(100.0, score);
    }

    private double calculateSkillScore(String studentSkills, String requiredSkills) {
        if (studentSkills == null || requiredSkills == null || requiredSkills.isBlank()) {
            return 50.0; // Default baseline
        }

        Set<String> requiredSet = new HashSet<>(Arrays.asList(requiredSkills.toLowerCase().split("\\s*,\\s*")));
        Set<String> studentSet = new HashSet<>(Arrays.asList(studentSkills.toLowerCase().split("\\s*,\\s*")));

        long matches = requiredSet.stream().filter(studentSet::contains).count();
        
        if (requiredSet.isEmpty()) return 100.0;
        
        return (double) matches / requiredSet.size() * 100.0;
    }
}
