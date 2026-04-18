package com.uniintern.portal.company.repository;

import com.uniintern.portal.company.entity.Internship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {

    List<Internship> findByCompanyId(Long companyId);
    
    List<Internship> findByStatus(String status);
    
    List<Internship> findByCompanyIdAndSkillsWeightIsNotNull(Long companyId);
    
    List<Internship> findBySkillsWeightIsNotNull();
    
    List<Internship> findByCompanyIdAndStatus(Long companyId, String status);

}