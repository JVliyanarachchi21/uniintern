package com.uniintern.portal.student.repository;

import com.uniintern.portal.student.model.StudentApplication;
import com.uniintern.portal.student.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface StudentApplicationRepository extends JpaRepository<StudentApplication, Long> {
    List<StudentApplication> findByStudentId(Long studentId);
    
    @Query("SELECT a FROM StudentApplication a LEFT JOIN FETCH a.student WHERE a.internshipId = :internshipId")
    List<StudentApplication> findByInternshipId(@Param("internshipId") Long internshipId);

    @Query("SELECT a FROM StudentApplication a LEFT JOIN FETCH a.student WHERE a.id = :id")
    Optional<StudentApplication> findByIdWithStudent(@Param("id") Long id);

    List<StudentApplication> findByStatus(ApplicationStatus status);
    
    long countByInternshipId(Long internshipId);

    @Query("SELECT a FROM StudentApplication a LEFT JOIN FETCH a.student WHERE a.internshipId IN :internshipIds ORDER BY a.appliedAt DESC")
    List<StudentApplication> findByInternshipIdIn(@Param("internshipIds") List<Long> internshipIds);

    @Modifying
    @Transactional
    void deleteByInternshipId(Long internshipId);

    long countByInternshipIdInAndScoreGreaterThan(List<Long> internshipIds, Double score);

    @Query("SELECT a FROM StudentApplication a JOIN FETCH a.student WHERE a.status IN :statuses")
    List<StudentApplication> findByStatusInWithStudent(@Param("statuses") List<ApplicationStatus> statuses);
}
