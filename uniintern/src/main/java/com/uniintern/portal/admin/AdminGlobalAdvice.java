package com.uniintern.portal.admin;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Global Sentry Advice.
 * Automatically injects real-time intelligence alerts into every Admin module response.
 * This ensures the notification bell pulses red consistently across all screens.
 */
@ControllerAdvice(basePackages = "com.uniintern.portal.admin")
public class AdminGlobalAdvice {

    private final AdminAlertService adminAlertService;

    public AdminGlobalAdvice(AdminAlertService adminAlertService) {
        this.adminAlertService = adminAlertService;
    }

    @ModelAttribute
    public void addGlobalIntelligence(Model model) {
        // Trigger a fresh scan for new system events
        adminAlertService.scanForNewEvents();

        // Inject active alerts and count into the model
        List<AdminAlert> activeAlerts = adminAlertService.getActiveAlerts();
        model.addAttribute("newAlerts", activeAlerts);
        model.addAttribute("alertCount", adminAlertService.getUnreadCount());
    }
}
