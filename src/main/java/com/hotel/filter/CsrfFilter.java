package com.hotel.filter;

import com.hotel.config.SecurityUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter providing Cross-Site Request Forgery (CSRF) protection.
 * Automatically injects CSRF tokens into HTTP sessions and validates them on mutating requests.
 */
@WebFilter(filterName = "CsrfFilter", urlPatterns = "/*")
public class CsrfFilter implements Filter {

    public static final String CSRF_SESSION_ATTR = "csrfToken";
    public static final String CSRF_PARAM_NAME = "_csrf";
    public static final String CSRF_HEADER_NAME = "X-CSRF-TOKEN";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(true);
        String sessionToken = (String) session.getAttribute(CSRF_SESSION_ATTR);

        if (sessionToken == null || sessionToken.isEmpty()) {
            sessionToken = SecurityUtil.generateCsrfToken();
            session.setAttribute(CSRF_SESSION_ATTR, sessionToken);
        }

        // Expose token to request attributes for easy JSP EL access: ${csrfToken}
        httpRequest.setAttribute(CSRF_SESSION_ATTR, sessionToken);

        String method = httpRequest.getMethod().toUpperCase();

        // Mutating methods require validation
        if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
            String requestToken = httpRequest.getParameter(CSRF_PARAM_NAME);
            if (requestToken == null || requestToken.isEmpty()) {
                requestToken = httpRequest.getHeader(CSRF_HEADER_NAME);
            }

            // Exclude static assets or external endpoints if any, but validate all app POST actions
            if (requestToken == null || !sessionToken.equals(requestToken.trim())) {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpRequest.setAttribute("errorCode", HttpServletResponse.SC_FORBIDDEN);
                httpRequest.setAttribute("errorMessage", "Security verification failed (Invalid or missing CSRF token). Please refresh the page and try again.");
                httpRequest.getRequestDispatcher("/WEB-INF/views/error-500.jsp").forward(httpRequest, httpResponse);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
