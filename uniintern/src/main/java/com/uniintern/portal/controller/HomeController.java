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
    public String home(Model model) {
        // Fetch active home page banner promotions
        List<com.uniintern.portal.company.entity.Promotion> bannerPromos = promotionService.getActiveBannerPromotions();
        
        // Map to listings
        List<com.uniintern.portal.company.dto.InternshipListingDto> banners = bannerPromos.stream()
                .map(p -> internshipService.getApprovedInternshipsListings().stream()
                        .filter(i -> i.getId().equals(p.getInternshipId()))
                        .findFirst()
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
        
        model.addAttribute("banners", banners);
        return "home";
    }
}