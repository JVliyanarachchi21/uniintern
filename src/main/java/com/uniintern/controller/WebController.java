package com.uniintern.controller;

import com.uniintern.model.CourseInfo;
import com.uniintern.model.Question;
import com.uniintern.service.CourseDataService;
import com.uniintern.service.QuizDataService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WebController {

    @Autowired
    private CourseDataService courseDataService;

    @Autowired
    private QuizDataService quizDataService;

    // Landing page
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Enroll user
    @PostMapping("/enroll")
    public String enroll(@RequestParam String fullName,
                         @RequestParam String email,
                         HttpSession session) {
        session.setAttribute("userName", fullName);
        session.setAttribute("userEmail", email);
        return "redirect:/dashboard";
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/";
        }
        model.addAttribute("userName", userName);
        return "dashboard";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // Course video page
    @GetMapping("/course/{courseId}")
    public String course(@PathVariable String courseId,
                         HttpSession session,
                         Model model) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/";
        }

        CourseInfo course = courseDataService.getCourse(courseId);
        if (course == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("courseId", courseId);
        model.addAttribute("courseTitle", course.getTitle());
        model.addAttribute("courseDescription", course.getDescription());
        model.addAttribute("youtubeId", course.getYoutubeId());
        model.addAttribute("userName", userName);

        return "course";
    }

    // Quiz page
    @GetMapping("/quiz")
    public String quiz(@RequestParam String courseId,
                       HttpSession session,
                       Model model) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/";
        }

        CourseInfo course = courseDataService.getCourse(courseId);
        if (course == null) {
            return "redirect:/dashboard";
        }

        List<Question> quizData = quizDataService.getQuizData(courseId);
        if (quizData.isEmpty()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("courseId", courseId);
        model.addAttribute("courseTitle", course.getTitle());
        model.addAttribute("quizData", quizData);
        model.addAttribute("userName", userName);

        return "quiz";
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
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/";
        }

        CourseInfo course = courseDataService.getCourse(courseId);
        if (course == null) {
            return "redirect:/dashboard";
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

        return "results";
    }

    // Certificate page
    @GetMapping("/certificate")
    public String certificate(@RequestParam String courseId,
                              HttpSession session,
                              Model model) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/";
        }

        CourseInfo course = courseDataService.getCourse(courseId);
        if (course == null) {
            return "redirect:/dashboard";
        }

        // Check if user passed
        Boolean lastPassed = (Boolean) session.getAttribute("lastPassed");
        String lastGrade = (String) session.getAttribute("lastGrade");

        if (lastPassed == null || !lastPassed) {
            return "redirect:/dashboard";
        }

        model.addAttribute("courseId", courseId);
        model.addAttribute("courseTitle", course.getTitle());
        model.addAttribute("userName", userName);
        model.addAttribute("grade", lastGrade);

        return "certificate";
    }
}
