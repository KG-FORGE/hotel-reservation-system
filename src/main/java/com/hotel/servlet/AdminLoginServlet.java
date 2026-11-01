package com.hotel.servlet;

import com.hotel.filter.AdminAuthFilter;
import com.hotel.model.AdminUser;
import com.hotel.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller managing administrative authentication and login sessions.
 */
@WebServlet(name = "AdminLoginServlet", urlPatterns = "/admin/login")
public class AdminLoginServlet extends HttpServlet {

    private final AuthService authService = AuthService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AdminAuthFilter.ADMIN_SESSION_KEY) != null) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        String errorParam = request.getParameter("error");
        if ("auth_required".equals(errorParam)) {
            request.setAttribute("errorMessage", "Administrative access required. Please sign in.");
        } else if ("session_expired".equals(errorParam)) {
            request.setAttribute("errorMessage", "Session expired. Please sign in again.");
        }

        String logoutParam = request.getParameter("logout");
        if ("success".equals(logoutParam)) {
            request.setAttribute("successMessage", "You have been successfully logged out.");
        }

        request.setAttribute("pageTitle", "Admin Sign In | KG Grand Hotel");
        request.getRequestDispatcher("/WEB-INF/views/admin-login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required.");
            request.setAttribute("enteredUsername", username != null ? username.trim() : "");
            request.setAttribute("pageTitle", "Admin Sign In | KG Grand Hotel");
            request.getRequestDispatcher("/WEB-INF/views/admin-login.jsp").forward(request, response);
            return;
        }

        Optional<AdminUser> adminOpt = authService.authenticate(username.trim(), password);

        if (adminOpt.isPresent()) {
            HttpSession oldSession = request.getSession(false);
            String existingCsrf = (oldSession != null) ? (String) oldSession.getAttribute("csrfToken") : null;

            if (oldSession != null) {
                request.changeSessionId();
            }
            HttpSession session = request.getSession(true);
            if (existingCsrf != null) {
                session.setAttribute("csrfToken", existingCsrf);
            }
            session.setAttribute(AdminAuthFilter.ADMIN_SESSION_KEY, adminOpt.get());
            // Session timeout 30 minutes
            session.setMaxInactiveInterval(30 * 60);

            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            request.setAttribute("errorMessage", "Invalid administrator username or password.");
            request.setAttribute("enteredUsername", username.trim());
            request.setAttribute("pageTitle", "Admin Sign In | KG Grand Hotel");
            request.getRequestDispatcher("/WEB-INF/views/admin-login.jsp").forward(request, response);
        }
    }
}
