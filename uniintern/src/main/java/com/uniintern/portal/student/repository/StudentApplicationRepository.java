package com.uniintern.portal.student.repository;

import com.uniintern.portal.student.model.StudentApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface StudentApplicationRepository extends JpaRepository<StudentApplication, Long> {
    List<StudentApplication> findByStudentId(Long studentId);
    List<StudentApplication> findByStatus(com.uniintern.portal.student.model.ApplicationStatus status);
    List<StudentApplication> findByInternshipId(Long internshipId);
    long countByInternshipId(Long internshipId);

    @Modifying
    @Transactional
    void deleteByInternshipId(Long internshipId);
}