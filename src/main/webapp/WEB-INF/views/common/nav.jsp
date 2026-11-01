<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="navbar">
    <div class="container nav-container">
        <a href="${pageContext.request.contextPath}/" class="brand-wrap" aria-label="KG Grand Hotel Home">
            <div class="brand-mark">KG</div>
            <div>
                <span class="brand-title">KG Grand Hotel</span>
                <span class="brand-tagline">Luxury &amp; Suites</span>
            </div>
        </a>

        <nav aria-label="Primary Navigation">
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/" class="nav-link ${pageContext.request.requestURI.endsWith('/index.jsp') || pageContext.request.servletPath == '' || pageContext.request.servletPath == '/home' ? 'active' : ''}">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/rooms" class="nav-link ${pageContext.request.servletPath == '/rooms' ? 'active' : ''}">Our Rooms</a></li>
                <li><a href="${pageContext.request.contextPath}/my-bookings" class="nav-link ${pageContext.request.servletPath == '/my-bookings' ? 'active' : ''}">My Bookings</a></li>
                <li><a href="${pageContext.request.contextPath}/#about" class="nav-link">About Us</a></li>
                <li><a href="${pageContext.request.contextPath}/#contact" class="nav-link">Contact</a></li>
            </ul>
        </nav>

        <div class="nav-actions">
            <c:choose>
                <c:when test="${not empty sessionScope.adminUser}">
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline btn-sm">
                        Admin Portal
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/admin/login" class="btn btn-outline btn-sm" title="Administrator Portal Login">
                        Admin Login
                    </a>
                </c:otherwise>
            </c:choose>
            <a href="${pageContext.request.contextPath}/booking/new" class="btn btn-gold btn-sm">
                Book a Stay
            </a>
        </div>
    </div>
</header>
