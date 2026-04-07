package com.uniintern.portal.company.repository;

import com.uniintern.portal.company.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    java.util.List<Interview> findByStudentId(Long studentId);
}
