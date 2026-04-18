package com.uniintern.portal.cv_filering.repo;

import com.uniintern.portal.cv_filering.model.FilteringLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilteringLogRepository extends JpaRepository<FilteringLog, Long> {
    
    List<FilteringLog> findAllByOrderByRunAtDesc();
    
    List<FilteringLog> findByUserRoleOrderByRunAtDesc(String userRole);
}
