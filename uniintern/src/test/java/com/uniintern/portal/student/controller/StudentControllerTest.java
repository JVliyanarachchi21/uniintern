package com.uniintern.portal.student.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("ST-001: Student Dashboard Loads")
    public void testStudentDashboardLoads() throws Exception {
        mockMvc.perform(get("/student/dashboard")
                .sessionAttr("loggedInStudentId", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("student/dashboard"))
                .andExpect(model().attributeExists("fullName", "profileCompletion"));
    }

    @Test
    @DisplayName("ST-002: Internships Listing Page Loads")
    public void testInternshipsPageLoads() throws Exception {
        mockMvc.perform(get("/student/internships")
                .sessionAttr("loggedInStudentId", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("student/internships"))
                .andExpect(model().attributeExists("internships"));
    }

    @Test
    @DisplayName("ST-003: My Applications Page Loads")
    public void testMyApplicationsPageLoads() throws Exception {
        mockMvc.perform(get("/student/applications")
                .sessionAttr("loggedInStudentId", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("student/my-applications"))
                .andExpect(model().attributeExists("applications"));
    }

    @Test
    @DisplayName("ST-004: Student Profile Page Loads")
    public void testStudentProfileLoads() throws Exception {
        mockMvc.perform(get("/student/profile")
                .sessionAttr("loggedInStudentId", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("student/profile"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    @DisplayName("ST-005: Student Interviews Page Loads")
    public void testStudentInterviewsLoads() throws Exception {
        mockMvc.perform(get("/student/interviews")
                .sessionAttr("loggedInStudentId", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("student/interviews"))
                .andExpect(model().attributeExists("interviewList"));
    }

    @Test
    @DisplayName("ST-006: Security - Unauthorized Access Redirects to Login")
    public void testUnauthorizedAccessRedirects() throws Exception {
        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/login"));
    }
}
