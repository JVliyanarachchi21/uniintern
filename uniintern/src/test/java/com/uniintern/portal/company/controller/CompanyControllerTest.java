package com.uniintern.portal.company.controller;

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
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // TC-001: Check if the Talent Pool page opens correctly for a logged-in company.
    @Test
    @DisplayName("TC-001: Public Talent Pool Page")
    public void testTalentPoolPageLoads() throws Exception {
        mockMvc.perform(get("/company/applicants")
                .sessionAttr("loggedInCompanyId", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("company/applicants"))
                .andExpect(model().attributeExists("applicants"));
    }

    // TC-002: Check if the 'My Internships' page loads successfully for the company.
    @Test
    @DisplayName("TC-002: Internship Management View")
    public void testViewInternshipsPageLoads() throws Exception {
        mockMvc.perform(get("/company/internships")
                .sessionAttr("loggedInCompanyId", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("company/internships"));
    }

    // TC-003: Check if users who are NOT logged in are blocked and sent to the login page.
    @Test
    @DisplayName("TC-003: Unauthorized Access Security Check")
    public void testUnauthorizedAccessRedirects() throws Exception {
        mockMvc.perform(get("/company/applicants"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/company/login"));
    }
    
    // TC-004: Check if the Detailed Student Profile page opens properly when clicked.
    @Test
    @DisplayName("TC-004: Applicant Detailed Profile Mapping")
    public void testViewApplicantDetailMapping() throws Exception {
        mockMvc.perform(get("/company/applicants/view/1")
                .sessionAttr("loggedInCompanyId", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("company/view-applicant"));
    }

    // TC-005: Check if the 'Post New Internship' page is available for the company.
    @Test
    @DisplayName("TC-005: Post New Internship Page Availability")
    public void testPostInternshipPageAvailability() throws Exception {
        mockMvc.perform(get("/company/internships/new")
                .sessionAttr("loggedInCompanyId", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("company/new-internship"));
    }
}
