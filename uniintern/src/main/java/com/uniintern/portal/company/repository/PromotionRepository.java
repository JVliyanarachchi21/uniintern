package com.uniintern.portal.company.repository;

import com.uniintern.portal.company.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    
    List<Promotion> findByCompanyId(Long companyId);
    
    List<Promotion> findByInternshipIdAndStatus(Long internshipId, String status);
    
    // Find all active promotions for a specific date (usually now)
    List<Promotion> findByStatusAndEndDateAfter(String status, LocalDateTime date);
}
