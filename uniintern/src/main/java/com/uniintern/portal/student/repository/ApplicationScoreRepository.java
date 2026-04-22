package com.uniintern.portal.student.repository;

import com.uniintern.portal.student.model.ApplicationScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ApplicationScoreRepository extends JpaRepository<ApplicationScore, Long> {
    List<ApplicationScore> findByApplicationId(Long applicationId);

    @Modifying
    @Transactional
    void deleteByApplicationId(Long applicationId);
}
