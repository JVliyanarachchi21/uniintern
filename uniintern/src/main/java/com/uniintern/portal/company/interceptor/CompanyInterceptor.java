package com.uniintern.portal.company.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CompanyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Allow public company endpoints
        if (uri.startsWith("/company/login") || 
            uri.startsWith("/company/register") || 
            uri.startsWith("/company/verify") ||
            uri.startsWith("/company/internships/listing") ||
            uri.startsWith("/company/internships/preview") ||
            uri.startsWith("/company/internships/application-template") ||
            uri.startsWith("/company/css/") ||
            uri.startsWith("/company/js/") ||
            uri.startsWith("/company/images/")) {
            return true;
        }

        // Only enforce logic for company endpoints
        if (uri.startsWith("/company")) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("loggedInCompanyId") != null) {
                return true;
            }
            
            // Redirect to login if session missing or company ID not found
            response.sendRedirect("/company/login");
            return false;
        }

        return true;
    }
}
