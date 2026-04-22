package com.uniintern.portal.admin;

import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.repository.CompanyRepository;
import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.repository.InternshipRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminAlertService {

    private final AdminAlertRepository adminAlertRepository;
    private final CompanyRepository companyRepository;
    private final InternshipRepository internshipRepository;

    public AdminAlertService(AdminAlertRepository adminAlertRepository,
                             CompanyRepository companyRepository,
                             InternshipRepository internshipRepository) {
        this.adminAlertRepository = adminAlertRepository;
        this.companyRepository = companyRepository;
        this.internshipRepository = internshipRepository;
    }

    /**
     * The Autonomous Intelligence Sentry.
     * Scans the system for new activity using Zero-Touch detection logic.
     */
    public void scanForNewEvents() {
        // Last 24 hours window for "fresh" alerts
        LocalDateTime window = LocalDateTime.now().minusDays(1);

        // 1. Detect New Companies
        List<Company> newCompanies = companyRepository.findAll().stream()
                .filter(c -> c.getCreatedAt() != null && c.getCreatedAt().isAfter(window))
                .filter(c -> "PENDING_VERIFICATION".equals(c.getStatus()))
                .toList();

        for (Company c : newCompanies) {
            String msg = "SECURITY ALERT: New Company '" + c.getCompanyName() + "' is internally registered and waiting for Admin verification.";
            if (!hasExistingAlert(msg)) {
                adminAlertRepository.save(new AdminAlert("REGISTRATION", "HIGH", msg, "/admin/reports"));
            }
        }

        // 2. Detect New Internships
        List<Internship> newInternships = internshipRepository.findAll().stream()
                .filter(i -> i.getCreatedAt() != null && i.getCreatedAt().isAfter(window))
                .filter(i -> "PENDING_ADMIN_APPROVAL".equals(i.getStatus()))
                .toList();

        for (Internship i : newInternships) {
            String msg = "ACADEMIC UPDATE: New Internship '" + i.getTitle() + "' posted. Review required for student safety.";
            if (!hasExistingAlert(msg)) {
                adminAlertRepository.save(new AdminAlert("INTERNSHIP", "MEDIUM", msg, "/admin/reports"));
            }
        }
    }

    private boolean hasExistingAlert(String message) {
        return adminAlertRepository.findAll().stream()
                .anyMatch(a -> a.getMessage().equals(message));
    }

    public List<AdminAlert> getActiveAlerts() {
        return adminAlertRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    public long getUnreadCount() {
        return adminAlertRepository.countByIsReadFalse();
    }

    public void markAllAsRead() {
        List<AdminAlert> unread = adminAlertRepository.findByIsReadTrue(); // Use this as dummy to fetch and save?
        // Better:
        List<AdminAlert> all = adminAlertRepository.findAll();
        all.forEach(a -> a.setRead(true));
        adminAlertRepository.saveAll(all);
    }
}
