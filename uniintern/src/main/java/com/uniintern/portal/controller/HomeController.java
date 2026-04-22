package com.uniintern.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    
    @Autowired
    private com.uniintern.portal.company.service.PromotionService promotionService;
    
    @Autowired
    private com.uniintern.portal.company.service.InternshipService internshipService;

    @GetMapping("/")
    public String home(jakarta.servlet.http.HttpSession session, Model model) {
        // Fetch active home page banner promotions
        List<com.uniintern.portal.company.entity.Promotion> bannerPromos = promotionService.getActiveBannerPromotions();
        
        List<Long> promotedInternshipIds = bannerPromos.stream()
                .map(com.uniintern.portal.company.entity.Promotion::getInternshipId)
                .distinct()
                .collect(Collectors.toList());
        
        // Map to listings effectively and guaranteeing uniqueness
        List<com.uniintern.portal.company.dto.InternshipListingDto> banners = internshipService.getApprovedInternshipsListings(null, null, null).stream()
                .filter(i -> promotedInternshipIds.contains(i.getId()))
                .collect(Collectors.toList());
        
        model.addAttribute("banners", banners);
        
        // Check if student is logged in
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId != null) {
            model.addAttribute("isLoggedInStudent", true);
            model.addAttribute("studentName", session.getAttribute("studentName"));
        } else {
            model.addAttribute("isLoggedInStudent", false);
        }
        
        // Check if company is logged in
        Long companyId = (Long) session.getAttribute("loggedInCompanyId");
        if (companyId != null) {
            model.addAttribute("isLoggedInCompany", true);
        } else {
            model.addAttribute("isLoggedInCompany", false);
        }
        
        return "home";
    }
}