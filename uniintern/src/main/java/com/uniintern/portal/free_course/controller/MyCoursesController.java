package com.uniintern.portal.free_course.controller;

import com.uniintern.portal.free_course.entity.FreeCourseEnrollment;
import com.uniintern.portal.free_course.model.CourseInfo;
import com.uniintern.portal.free_course.repository.FreeCourseEnrollmentRepository;
import com.uniintern.portal.free_course.service.CourseDataService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student/courses")
public class MyCoursesController {

    @Autowired
    private FreeCourseEnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseDataService courseDataService;

    @GetMapping("/my-courses")
    public String myCourses(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) {
            return "redirect:/student/login";
        }

        String userName = (String) session.getAttribute("studentName");
        if (userName == null) {
            userName = "Student";
        }

        // Get all enrollments for this student
        List<FreeCourseEnrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        // Build course list with enrollment details
        List<Map<String, Object>> myCoursesList = enrollments.stream().map(enrollment -> {
            Map<String, Object> courseMap = new HashMap<>();
            CourseInfo course = courseDataService.getCourse(enrollment.getCourseId());
            
            if (course != null) {
                courseMap.put("courseId", enrollment.getCourseId());
                courseMap.put("title", course.getTitle());
                courseMap.put("description", course.getDescription());
                courseMap.put("duration", course.getDuration());
                courseMap.put("enrolledAt", enrollment.getEnrolledAt());
                courseMap.put("completed", enrollment.getCompletedAt() != null);
                courseMap.put("passed", enrollment.isPassed());
                courseMap.put("grade", enrollment.getGrade());
                
                if (enrollment.getCompletedAt() != null) {
                    courseMap.put("completedAt", enrollment.getCompletedAt());
                }
            }
            
            return courseMap;
        }).collect(Collectors.toList());

        // Sort by most recently enrolled
        myCoursesList.sort((a, b) -> {
            Date dateA = (Date) a.get("enrolledAt");
            Date dateB = (Date) b.get("enrolledAt");
            return dateB.compareTo(dateA);
        });

        model.addAttribute("userName", userName);
        model.addAttribute("myCourses", myCoursesList);
        model.addAttribute("totalEnrolled", myCoursesList.size());
        model.addAttribute("totalCompleted", myCoursesList.stream().filter(c -> (Boolean) c.get("completed")).count());
        model.addAttribute("totalPassed", myCoursesList.stream().filter(c -> (Boolean) c.get("passed")).count());

        return "free_course/my-courses";
    }
}
