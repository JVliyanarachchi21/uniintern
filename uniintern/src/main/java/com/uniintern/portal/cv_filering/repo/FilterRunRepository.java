package com.uniintern.portal.cv_filering.repo;

import com.uniintern.portal.cv_filering.model.FilterRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilterRunRepository extends JpaRepository<FilterRun, Long> {
    List<FilterRun> findAllByOrderByFinishedAtDesc();
}
