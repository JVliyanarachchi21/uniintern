package com.uniintern.portal.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private static final String ADMIN_EMAIL = "admin@uniintern.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("email", "");
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            HttpSession session,
            Model model) {

        String cleanEmail = email == null ? "" : email.trim();
        String cleanPassword = password == null ? "" : password.trim();

        boolean hasError = false;

        model.addAttribute("email", cleanEmail);

        if (cleanEmail.isBlank()) {
            model.addAttribute("emailError", "Admin email is required.");
            hasError = true;
        } else if (!cleanEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            model.addAttribute("emailError", "Enter a valid admin email address.");
            hasError = true;
        }

        if (cleanPassword.isBlank()) {
            model.addAttribute("passwordError", "Password is required.");
            hasError = true;
        } else if (cleanPassword.length() < 8) {
            model.addAttribute("passwordError", "Password must be at least 8 characters.");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("formError", "Please fix the highlighted errors and try again.");
            return "admin/login";
        }

        if (!cleanEmail.equalsIgnoreCase(ADMIN_EMAIL) || !cleanPassword.equals(ADMIN_PASSWORD)) {
            model.addAttribute("formError", "Invalid admin credentials.");
            return "admin/login";
        }

        session.setAttribute("adminLoggedIn", true);
        session.setAttribute("adminEmail", ADMIN_EMAIL);

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}