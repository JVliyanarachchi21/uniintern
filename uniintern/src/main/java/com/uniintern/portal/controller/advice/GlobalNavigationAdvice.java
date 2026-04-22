package com.uniintern.portal.controller.advice;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalNavigationAdvice {

    @ModelAttribute("isLoggedInCompany")
    public boolean isLoggedInCompany(HttpSession session) {
        Object cid = session.getAttribute("loggedInCompanyId");
        boolean loggedIn = (cid != null);
        System.out.println("[NAV-ADVICE] Company Logged In Check: " + loggedIn + (loggedIn ? " (ID: " + cid + ")" : ""));
        return loggedIn;
    }

    @ModelAttribute("isLoggedInStudent")
    public boolean isLoggedInStudent(HttpSession session) {
        Object sid = session.getAttribute("loggedInStudentId");
        boolean loggedIn = (sid != null);
        System.out.println("[NAV-ADVICE] Student Logged In Check: " + loggedIn + (loggedIn ? " (ID: " + sid + ")" : ""));
        return loggedIn;
    }
}
