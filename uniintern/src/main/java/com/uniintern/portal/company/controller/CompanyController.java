package com.uniintern.portal.company.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "company/dashboard";
    }

    @GetMapping("/profile")
    public String profile() {
        return "company/profile";
    }

    @GetMapping("/internships/new")
    public String newInternship() {
        return "company/new-internship";
    }

    @GetMapping("/internships")
    public String internships() {
        return "company/internships";
    }

    @GetMapping("/applicants")
    public String applicants() {
        return "company/applicants";
    }

    @GetMapping("/promotions")
    public String promotions() {
        return "company/promotions";
    }

    @GetMapping("/payments/checkout")
    public String checkout() {
        return "company/checkout";
    }

    @GetMapping("/payments/history")
    public String paymentHistory() {
        return "company/payment-history";
    }

    @GetMapping("/notifications")
    public String notifications() {
        return "company/notifications";
    }

    @GetMapping("/settings")
    public String settings() {
        return "company/settings";
    }
}