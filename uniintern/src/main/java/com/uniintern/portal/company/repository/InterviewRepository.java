package com.uniintern.portal.company.repository;

import com.uniintern.portal.company.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    java.util.List<Interview> findByStudentId(Long studentId);
    long countByStatus(String status);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Interview i WHERE CAST(i.interviewDateTime AS date) = CAST(:date AS date)")
    long countInterviewsByDate(@org.springframework.data.repository.query.Param("date") java.time.LocalDateTime date);
}
