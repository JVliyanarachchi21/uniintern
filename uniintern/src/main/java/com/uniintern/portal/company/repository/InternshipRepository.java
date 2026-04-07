package com.uniintern.portal.company.repository;

import com.uniintern.portal.company.entity.Internship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {

    List<Internship> findByCompanyId(Long companyId);
    
    List<Internship> findByStatus(String status);
    
    long countByCompanyIdAndStatus(Long companyId, String status);
    
    long countByCompanyIdAndStatusAndApprovedNotificationSeenFalse(Long companyId, String status);
    
    List<Internship> findByCompanyIdAndStatusAndApprovedNotificationSeenFalse(Long companyId, String status);
    
    long countByCompanyIdAndStatusAndRejectedNotificationSeenFalse(Long companyId, String status);
    
    List<Internship> findByCompanyIdAndStatusAndRejectedNotificationSeenFalse(Long companyId, String status);

    List<Internship> findByCompanyIdAndStatusInOrderByIdDesc(Long companyId, List<String> statuses);
}