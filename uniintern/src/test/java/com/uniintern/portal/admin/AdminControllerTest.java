package com.uniintern.portal.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSchedulingHubLoads() throws Exception {
        mockMvc.perform(get("/admin/schedule")
                .sessionAttr("adminLoggedIn", true)
                .sessionAttr("serverRunId", AdminInterceptor.SERVER_RUN_ID))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("approvedInternships"));
    }

    @Test
    public void testInterviewCreationSuccess() throws Exception {
        mockMvc.perform(post("/admin/schedule")
                .sessionAttr("adminLoggedIn", true)
                .sessionAttr("serverRunId", AdminInterceptor.SERVER_RUN_ID)
                .param("candidateName", "VIVA PROOF")
                .param("internshipId", "1")
                .param("datetime", "2030-12-01T14:00")
                .param("mode", "Online")
                .param("locationLink", "https://zoom.us/viva"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("showPopup", true));
    }

    @Test
    public void testNameValidation() throws Exception {
        mockMvc.perform(post("/admin/schedule")
                .sessionAttr("adminLoggedIn", true)
                .sessionAttr("serverRunId", AdminInterceptor.SERVER_RUN_ID)
                .param("candidateName", "A") 
                .param("internshipId", "1")
                .param("datetime", "2030-12-01T14:00"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("candidateNameError"));
    }

    @Test
    public void testPastDateValidation() throws Exception {
        mockMvc.perform(post("/admin/schedule")
                .sessionAttr("adminLoggedIn", true)
                .sessionAttr("serverRunId", AdminInterceptor.SERVER_RUN_ID)
                .param("candidateName", "VIVA PROOF")
                .param("internshipId", "1")
                .param("datetime", "2020-01-01T10:00"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("datetimeError"));
    }

    // --- NEW: EXPANDED MODULE COVERAGE ---

    @Test
    public void testAdminDashboardLoads() throws Exception {
        mockMvc.perform(get("/admin/dashboard")
                .sessionAttr("adminLoggedIn", true)
                .sessionAttr("serverRunId", AdminInterceptor.SERVER_RUN_ID))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/dashboard"));
    }

    @Test
    public void testNotificationHubLoads() throws Exception {
        mockMvc.perform(get("/admin/notifications")
                .sessionAttr("adminLoggedIn", true)
                .sessionAttr("serverRunId", AdminInterceptor.SERVER_RUN_ID))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/notifications"));
    }

    @Test
    public void testCompanyApprovalsPageLoads() throws Exception {
        mockMvc.perform(get("/admin/companies")
                .sessionAttr("adminLoggedIn", true)
                .sessionAttr("serverRunId", AdminInterceptor.SERVER_RUN_ID))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/company-approvals"));
    }

    @Test
    public void testInternshipApprovalsPageLoads() throws Exception {
        mockMvc.perform(get("/admin/internships")
                .sessionAttr("adminLoggedIn", true)
                .sessionAttr("serverRunId", AdminInterceptor.SERVER_RUN_ID))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/internship-approvals"));
    }

    @Test
    public void testSchedulingOverviewLoads() throws Exception {
        mockMvc.perform(get("/admin/scheduling")
                .sessionAttr("adminLoggedIn", true)
                .sessionAttr("serverRunId", AdminInterceptor.SERVER_RUN_ID))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/interview-scheduling"));
    }

    @Test
    public void testAdvancedSchedulingHubLoads() throws Exception {
        mockMvc.perform(get("/admin/scheduling/advanced")
                .sessionAttr("adminLoggedIn", true)
                .sessionAttr("serverRunId", AdminInterceptor.SERVER_RUN_ID))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/advanced-scheduling"));
    }
}
