package com.uniintern.portal.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    public static final String SERVER_RUN_ID = UUID.randomUUID().toString();

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String uri = request.getRequestURI();
        if (uri.startsWith("/admin/login") || uri.startsWith("/admin/logout")) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"))) {
            // Force re-login if the server has restarted (ID mismatch)
            String sessionRunId = (String) session.getAttribute("serverRunId");
            if (SERVER_RUN_ID.equals(sessionRunId)) {
                return true;
            }
        }

        response.sendRedirect("/admin/login");
        return false;
    }
}