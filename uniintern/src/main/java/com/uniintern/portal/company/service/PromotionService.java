package com.uniintern.portal.company.service;

import com.uniintern.portal.company.entity.Promotion;
import com.uniintern.portal.company.repository.PromotionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public Promotion createPromotion(Long internshipId, Long companyId, String type, Double price, int days) {
        Promotion promotion = new Promotion();
        promotion.setInternshipId(internshipId);
        promotion.setCompanyId(companyId);
        promotion.setType(type);
        promotion.setPrice(price);
        promotion.setStartDate(LocalDateTime.now());
        promotion.setEndDate(LocalDateTime.now().plusDays(days));
        promotion.setStatus("ACTIVE");
        return promotionRepository.save(promotion);
    }

    public List<Promotion> getActivePromotionsForCompany(Long companyId) {
        return promotionRepository.findByCompanyId(companyId).stream()
                .filter(p -> p.getEndDate().isAfter(LocalDateTime.now()) && "ACTIVE".equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean isInternshipPromoted(Long internshipId) {
        List<Promotion> promotions = promotionRepository.findByInternshipIdAndStatus(internshipId, "ACTIVE");
        return promotions.stream().anyMatch(p -> p.getEndDate().isAfter(LocalDateTime.now()));
    }

    public List<Long> getPromotedInternshipIds() {
        return promotionRepository.findByStatusAndEndDateAfter("ACTIVE", LocalDateTime.now())
                .stream()
                .map(Promotion::getInternshipId)
                .collect(Collectors.toList());
    }
}
