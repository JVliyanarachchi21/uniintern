package com.uniintern.portal.free_course.repository;

import com.uniintern.portal.free_course.entity.FreeCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FreeCourseEnrollmentRepository extends JpaRepository<FreeCourseEnrollment, Long> {
    
    List<FreeCourseEnrollment> findByStudentId(Long studentId);
    
    Optional<FreeCourseEnrollment> findByStudentIdAndCourseId(Long studentId, String courseId);
    
    boolean existsByStudentIdAndCourseId(Long studentId, String courseId);
}
