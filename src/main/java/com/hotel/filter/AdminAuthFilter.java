package com.hotel.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter securing all administrative routes (/admin/*).
 * Enforces session authentication and redirects unauthenticated users to the admin login page.
 */
@WebFilter(filterName = "AdminAuthFilter", urlPatterns = "/admin/*")
public class AdminAuthFilter implements Filter {

    public static final String ADMIN_SESSION_KEY = "adminUser";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String servletPath = httpRequest.getServletPath();

        // Permit login and logout endpoints without prior authentication
        if ("/admin/login".equals(servletPath) || "/admin/logout".equals(servletPath)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        Object adminObj = (session != null) ? session.getAttribute(ADMIN_SESSION_KEY) : null;
        boolean isLoggedInAdmin = (adminObj instanceof com.hotel.model.AdminUser);

        if (!isLoggedInAdmin) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/login?error=auth_required");
            return;
        }

        // Prevent browser caching of sensitive administrative views
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
