package com.uniintern.portal.student.repository;

import com.uniintern.portal.student.model.ApplicationScore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationScoreRepository extends JpaRepository<ApplicationScore, Long> {
    List<ApplicationScore> findByApplicationId(Long applicationId);
}
