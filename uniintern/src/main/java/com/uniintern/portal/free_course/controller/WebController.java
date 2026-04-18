package com.uniintern.portal.free_course.controller;

import com.uniintern.portal.free_course.model.CourseInfo;
import com.uniintern.portal.free_course.model.Question;
import com.uniintern.portal.free_course.service.CourseDataService;
import com.uniintern.portal.free_course.service.QuizDataService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student/courses")
public class WebController {

    @Autowired
    private CourseDataService courseDataService;

    @Autowired
    private QuizDataService quizDataService;

    // Landing page - redirect to student dashboard
    @GetMapping("/")
    public String index() {
        return "redirect:/student/courses/dashboard";
    }

    // Dashboard - List all available courses
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) {
            return "redirect:/student/login";
        }

        // Get student name from session (set by existing student login)
        String userName = (String) session.getAttribute("studentName");
        if (userName == null) {
            userName = "Student";
        }
        model.addAttribute("userName", userName);
        model.addAttribute("courses", courseDataService.getAllCourses());
        return "free_course/dashboard";
    }

    // Course video page
    @GetMapping("/view/{courseId}")
    public String course(@PathVariable String courseId,
                         HttpSession session,
                         Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) {
            return "redirect:/student/login";
        }

        String userName = (String) session.getAttribute("studentName");
        if (userName == null) {
            userName = "Student";
        }

        CourseInfo course = courseDataService.getCourse(courseId);
        if (course == null) {
            return "redirect:/student/courses/dashboard";
        }

        model.addAttribute("courseId", courseId);
        model.addAttribute("courseTitle", course.getTitle());
        model.addAttribute("courseDescription", course.getDescription());
        model.addAttribute("youtubeId", course.getYoutubeId());
        model.addAttribute("userName", userName);

        return "free_course/course";
    }

    // Quiz page
    @GetMapping("/quiz")
    public String quiz(@RequestParam String courseId,
                       HttpSession session,
                       Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) {
            return "redirect:/student/login";
        }

        String userName = (String) session.getAttribute("studentName");
        if (userName == null) {
            userName = "Student";
        }

        CourseInfo course = courseDataService.getCourse(courseId);
        if (course == null) {
            return "redirect:/student/courses/dashboard";
        }

        List<Question> quizData = quizDataService.getQuizData(courseId);
        if (quizData.isEmpty()) {
            return "redirect:/student/courses/dashboard";
        }

        model.addAttribute("courseId", courseId);
        model.addAttribute("courseTitle", course.getTitle());
        model.addAttribute("quizData", quizData);
        model.addAttribute("userName", userName);

        return "free_course/quiz";
    }

    // Results page
    @PostMapping("/results")
    public String results(@RequestParam String courseId,
                          @RequestParam int score,
                          @RequestParam int correctCount,
                          @RequestParam int incorrectCount,
                          @RequestParam String grade,
                          @RequestParam boolean passed,
                          HttpSession session,
                          Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) {
            return "redirect:/student/login";
        }

        String userName = (String) session.getAttribute("studentName");
        if (userName == null) {
            userName = "Student";
        }

        CourseInfo course = courseDataService.getCourse(courseId);
        if (course == null) {
            return "redirect:/student/courses/dashboard";
        }

        model.addAttribute("courseId", courseId);
        model.addAttribute("courseTitle", course.getTitle());
        model.addAttribute("score", score);
        model.addAttribute("correctCount", correctCount);
        model.addAttribute("incorrectCount", incorrectCount);
        model.addAttribute("grade", grade);
        model.addAttribute("passed", passed);
        model.addAttribute("userName", userName);

        // Store results in session for certificate page
        session.setAttribute("lastScore", score);
        session.setAttribute("lastGrade", grade);
        session.setAttribute("lastPassed", passed);

        return "free_course/results";
    }

    // Certificate page
    @GetMapping("/certificate")
    public String certificate(@RequestParam String courseId,
                              HttpSession session,
                              Model model) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) {
            return "redirect:/student/login";
        }

        String userName = (String) session.getAttribute("studentName");
        if (userName == null) {
            userName = "Student";
        }

        CourseInfo course = courseDataService.getCourse(courseId);
        if (course == null) {
            return "redirect:/student/courses/dashboard";
        }

        // Check if user passed
        Boolean lastPassed = (Boolean) session.getAttribute("lastPassed");
        String lastGrade = (String) session.getAttribute("lastGrade");

        if (lastPassed == null || !lastPassed) {
            return "redirect:/student/courses/dashboard";
        }

        model.addAttribute("courseId", courseId);
        model.addAttribute("courseTitle", course.getTitle());
        model.addAttribute("userName", userName);
        model.addAttribute("grade", lastGrade);

        return "free_course/certificate";
    }
}
