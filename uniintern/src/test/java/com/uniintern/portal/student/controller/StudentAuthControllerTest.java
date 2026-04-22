package com.uniintern.portal.student.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("AUTH-001: Login Page Accessibility")
    public void testLoginPageLoads() throws Exception {
        mockMvc.perform(get("/student/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/login"));
    }

    @Test
    @DisplayName("AUTH-002: Registration Page Accessibility")
    public void testRegistrationPageLoads() throws Exception {
        mockMvc.perform(get("/student/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/register"));
    }

    @Test
    @DisplayName("AUTH-003: Login Failure - Invalid Credentials")
    public void testLoginFailure() throws Exception {
        mockMvc.perform(post("/student/login")
                .param("email", "wrong@example.com")
                .param("password", "wrongpass"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("AUTH-004: OTP Verification Page Accessibility")
    public void testVerifyOtpPageLoads() throws Exception {
        mockMvc.perform(get("/student/verify-otp")
                .param("email", "test@example.com")
                .param("otp", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/verify-otp"));
    }
}
