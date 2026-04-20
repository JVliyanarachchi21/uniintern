package com.uniintern.portal.student.repository;

import com.uniintern.portal.student.model.StudentApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StudentApplicationRepository extends JpaRepository<StudentApplication, Long> {
    List<StudentApplication> findByStudentId(Long studentId);
    @Query("SELECT a FROM StudentApplication a LEFT JOIN FETCH a.student WHERE a.internshipId = :internshipId")
    List<StudentApplication> findByInternshipId(@Param("internshipId") Long internshipId);

    List<StudentApplication> findByStatus(com.uniintern.portal.student.model.ApplicationStatus status);
    long countByInternshipId(Long internshipId);

    @Modifying
    @Transactional
    void deleteByInternshipId(Long internshipId);
}