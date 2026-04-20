package com.uniintern.portal.cv_filering.repo;

import com.uniintern.portal.cv_filering.model.FilterResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilterResultRepository extends JpaRepository<FilterResult, Long> {
    List<FilterResult> findByRunIdOrderByRankNumberAsc(Long runId);
}
