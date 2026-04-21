package com.uniintern.portal.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdminAlertRepository extends JpaRepository<AdminAlert, Long> {
    List<AdminAlert> findByIsReadFalseOrderByCreatedAtDesc();
    
    // Explicitly for the UI badge
    long countByIsReadFalse();
    
    // For cleaning up old alerts
    List<AdminAlert> findByIsReadTrue();
}
