package com.uniintern.portal.company.service;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.dto.InternshipListingDto;
import com.uniintern.portal.company.repository.InternshipRepository;
import com.uniintern.portal.company.repository.PromotionRepository;
import com.uniintern.portal.student.repository.StudentApplicationRepository;
import com.uniintern.portal.student.repository.ApplicationScoreRepository;
import com.uniintern.portal.student.model.StudentApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InternshipService {

    @Autowired
    private InternshipRepository internshipRepository;
    
    @Autowired
    private CompanyService companyService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private StudentApplicationRepository studentApplicationRepository;

    @Autowired
    private ApplicationScoreRepository applicationScoreRepository;

    // Save internship
    public Internship save(Internship internship) {
        return internshipRepository.save(internship);
    }

    // Get internships by company
    public List<Internship> getByCompanyId(Long companyId) {
        return internshipRepository.findByCompanyId(companyId);
    }

    public java.util.Map<Long, Long> getApplicantCounts(java.util.List<Long> internshipIds) {
        java.util.Map<Long, Long> counts = new java.util.HashMap<>();
        for (Long id : internshipIds) {
            counts.put(id, studentApplicationRepository.countByInternshipId(id));
        }
        return counts;
    }

    // Get all internships
    public List<Internship> getAll() {
        return internshipRepository.findAll();
    }

    // Get approved internships
    public List<InternshipListingDto> getApprovedInternshipsListings(String keyword, Long companyId, String type) {
        List<Internship> approvedList = internshipRepository.findByStatus("APPROVED");
        
        if (companyId != null) {
            approvedList = approvedList.stream().filter(i -> companyId.equals(i.getCompanyId())).collect(Collectors.toList());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase();
            approvedList = approvedList.stream().filter(i -> {
                boolean matchMain = (i.getTitle() != null && i.getTitle().toLowerCase().contains(kw)) ||
                                    (i.getDescription() != null && i.getDescription().toLowerCase().contains(kw));
                if (matchMain) return true;
                if (i.getCompanyId() != null) {
                    Company company = companyService.findById(i.getCompanyId());
                    return company != null && company.getCompanyName() != null && company.getCompanyName().toLowerCase().contains(kw);
                }
                return false;
            }).collect(Collectors.toList());
        }
        if (type != null && !type.trim().isEmpty() && !type.equals("All Types")) {
            String typ = type.toLowerCase();
            approvedList = approvedList.stream().filter(i -> 
                i.getTitle() != null && i.getTitle().toLowerCase().contains(typ)
            ).collect(Collectors.toList());
        }
        
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

    // Delete internship with cascading cleanup
    @Transactional
    public void delete(Long id) {
        // 1. Delete Promotions
        promotionRepository.deleteByInternshipId(id);

        // 2. Cleanup Student Applications and their Scores
        List<StudentApplication> applications = studentApplicationRepository.findByInternshipId(id);
        
        for (StudentApplication app : applications) {
            applicationScoreRepository.deleteByApplicationId(app.getId());
        }
        
        studentApplicationRepository.deleteByInternshipId(id);

        // 3. Delete Internship
        internshipRepository.deleteById(id);
    }

    public long countByCompanyIdAndStatus(Long companyId, String status) {
        return internshipRepository.countByCompanyIdAndStatus(companyId, status);
    }

    public long countNewlyApproved(Long companyId) {
        return internshipRepository.countByCompanyIdAndStatusAndApprovedNotificationSeenFalse(companyId, "APPROVED");
    }

    public List<Internship> getNewlyApproved(Long companyId) {
        return internshipRepository.findByCompanyIdAndStatusAndApprovedNotificationSeenFalse(companyId, "APPROVED");
    }

    public void markApprovedAsSeen(Long companyId) {
        List<Internship> newlyApproved = internshipRepository.findByCompanyIdAndStatusAndApprovedNotificationSeenFalse(companyId, "APPROVED");
        for (Internship i : newlyApproved) {
            i.setApprovedNotificationSeen(true);
            internshipRepository.save(i);
        }
    }

    public long countNewlyRejected(Long companyId) {
        return internshipRepository.countByCompanyIdAndStatusAndRejectedNotificationSeenFalse(companyId, "REJECTED");
    }

    public List<Internship> getNewlyRejected(Long companyId) {
        return internshipRepository.findByCompanyIdAndStatusAndRejectedNotificationSeenFalse(companyId, "REJECTED");
    }

    public void markRejectedAsSeen(Long companyId) {
        List<Internship> newlyRejected = internshipRepository.findByCompanyIdAndStatusAndRejectedNotificationSeenFalse(companyId, "REJECTED");
        for (Internship i : newlyRejected) {
            i.setRejectedNotificationSeen(true);
            internshipRepository.save(i);
        }
    }

    public List<Map<String, Object>> getActivityNotifications(Long companyId) {
        List<Internship> items = internshipRepository.findByCompanyIdAndStatusInOrderByIdDesc(companyId, List.of("APPROVED", "REJECTED"));
        List<Map<String, Object>> notifications = new java.util.ArrayList<>();
        
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

        for (Internship i : items) {
            Map<String, Object> n = new java.util.HashMap<>();
            boolean isNew = false;
            String type = "info";
            String icon = "bi-info-circle";
            String message = "";
            
            if ("APPROVED".equals(i.getStatus())) {
                message = "Success! Your internship '" + i.getTitle() + "' has been approved by the admin.";
                type = "success";
                icon = "bi-check-circle";
                isNew = !i.isApprovedNotificationSeen();
            } else if ("REJECTED".equals(i.getStatus())) {
                message = "Alert! Your internship '" + i.getTitle() + "' has been rejected by the admin.";
                type = "error";
                icon = "bi-exclamation-triangle";
                isNew = !i.isRejectedNotificationSeen();
            }
            
            n.put("message", message);
            n.put("date", i.getCreatedAt() != null ? i.getCreatedAt().format(formatter) : "Recently");
            n.put("type", type);
            n.put("icon", icon);
            n.put("isNew", isNew);
            n.put("id", i.getId());
            
            notifications.add(n);
        }
        return notifications;
    }

}