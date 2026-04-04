package com.uniintern.portal.company.service;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.dto.InternshipListingDto;
import com.uniintern.portal.company.repository.InternshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InternshipService {

    @Autowired
    private InternshipRepository internshipRepository;
    
    @Autowired
    private CompanyService companyService;

    @Autowired
    private PromotionService promotionService;

    // Save internship
    public Internship save(Internship internship) {
        return internshipRepository.save(internship);
    }

    // Get internships by company
    public List<Internship> getByCompanyId(Long companyId) {
        return internshipRepository.findByCompanyId(companyId);
    }

    // Get all internships
    public List<Internship> getAll() {
        return internshipRepository.findAll();
    }

    // Get approved internships
    public List<InternshipListingDto> getApprovedInternshipsListings() {
        List<Internship> approvedList = internshipRepository.findByStatus("APPROVED");
        
        return approvedList.stream().map(internship -> {
            InternshipListingDto dto = new InternshipListingDto();
            dto.setId(internship.getId());
            dto.setTitle(internship.getTitle());
            dto.setLocation(internship.getLocation() != null ? internship.getLocation() : "Unknown");
            dto.setDuration(internship.getDuration() != null ? internship.getDuration() : "N/A");
            
            // Limit description to a snippet
            String desc = internship.getDescription();
            if (desc != null && desc.length() > 150) {
                dto.setDescriptionSnippet(desc.substring(0, 150) + "...");
            } else {
                dto.setDescriptionSnippet(desc != null ? desc : "");
            }
            
            // Calculate posted ago string
            if (internship.getCreatedAt() != null) {
                long days = ChronoUnit.DAYS.between(internship.getCreatedAt(), LocalDateTime.now());
                if (days == 0) {
                    dto.setPostedAgo("Posted Today");
                } else if (days == 1) {
                    dto.setPostedAgo("Posted 1 day ago");
                } else {
                    dto.setPostedAgo("Posted " + days + " days ago");
                }
            } else {
                dto.setPostedAgo("Recent");
            }
            
            // Fetch company details
            Company company = null;
            if (internship.getCompanyId() != null) {
                company = companyService.findById(internship.getCompanyId());
            }
            
            if (company != null) {
                dto.setCompanyName(company.getCompanyName());
                dto.setLogoPath(company.getLogoPath());
            } else {
                dto.setCompanyName("Unknown Company");
            }
            
            // Set promotion status
            dto.setPromoted(promotionService.isInternshipPromoted(internship.getId()));
            
            return dto;
        }).sorted((a, b) -> {
            // Promoted first
            if (a.isPromoted() && !b.isPromoted()) return -1;
            if (!a.isPromoted() && b.isPromoted()) return 1;
            // Then newest first (using ID as a proxy for chronology)
            return b.getId().compareTo(a.getId());
        }).collect(Collectors.toList());
    }

    //GET by id
    public Internship getById(Long id) {
        return internshipRepository.findById(id).orElse(null);
    }

    // Delete internship
    public void delete(Long id) {
        internshipRepository.deleteById(id);
    }
}