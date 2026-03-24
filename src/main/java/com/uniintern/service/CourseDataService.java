package com.uniintern.service;

import com.uniintern.model.CourseInfo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseDataService {

    private final Map<String, CourseInfo> courses = new HashMap<>();

    public CourseDataService() {
        initializeCourses();
    }

    private void initializeCourses() {
        courses.put("sql", new CourseInfo(
            "sql",
            "SQL for Beginners",
            "7S_tz1z_5bA",
            "Learn Database Management and SQL queries from scratch.",
            "Database",
            "4 Hours"
        ));

        courses.put("python", new CourseInfo(
            "python",
            "Python Full Course",
            "_uQrJ0TkZlc",
            "Master Python programming, from basic syntax to advanced concepts.",
            "Terminal",
            "6 Hours"
        ));

        courses.put("htmlcss", new CourseInfo(
            "htmlcss",
            "HTML + CSS Full Course",
            "qz0aGYrrlhU",
            "Build responsive websites using modern HTML5 and CSS3.",
            "Layout",
            "5 Hours"
        ));

        courses.put("java", new CourseInfo(
            "java",
            "Java Full Course",
            "eIrMbAQSU34",
            "Comprehensive Java tutorial covering OOP, data structures, and more.",
            "Coffee",
            "8 Hours"
        ));

        courses.put("cpp", new CourseInfo(
            "cpp",
            "C++ Full Course",
            "vLnPwxZdW4Y",
            "Deep dive into C++ programming, memory management, and STL.",
            "Code",
            "7 Hours"
        ));

        courses.put("cyber", new CourseInfo(
            "cyber",
            "Cyber Security Full Course",
            "U_P23SqJaDc",
            "Learn ethical hacking, network security, and cryptography.",
            "Shield",
            "10 Hours"
        ));
    }

    public CourseInfo getCourse(String courseId) {
        return courses.get(courseId);
    }

    public List<CourseInfo> getAllCourses() {
        return new ArrayList<>(courses.values());
    }

    public boolean hasCourse(String courseId) {
        return courses.containsKey(courseId);
    }
}
