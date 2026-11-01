<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">

<header class="navbar">
    <div class="container nav-container">
        <a href="${pageContext.request.contextPath}/" class="brand-wrap">
            <div class="brand-mark">KG</div>
            <div>
                <span class="brand-title">KG Grand Hotel</span>
                <span class="brand-tagline">Administrative Portal</span>
            </div>
        </a>
        <a href="${pageContext.request.contextPath}/" class="btn btn-outline btn-sm">
            &larr; Back to Guest Site
        </a>
    </div>
</header>

<main class="login-wrap">
    <div class="login-card">
        <div class="login-header">
            <div class="brand-mark" style="margin: 0 auto 16px auto; width: 52px; height: 52px; font-size: 1.4rem;">KG</div>
            <h1 style="font-size: 1.85rem; margin-bottom: 6px;">Staff Sign In</h1>
            <p style="color: var(--slate); font-size: 0.9rem;">Management &amp; Room Control Center</p>
        </div>

        <jsp:include page="common/alerts.jsp" />

        <!-- Demonstration Notice for College Evaluator -->
        <div class="demo-box">
            <strong>&#128273; Demonstration Credentials:</strong><br>
            Username: <code>admin</code><br>
            Password: <code>Admin@Hotel2026</code><br>
            <small style="color: var(--slate-dark); display:block; margin-top:4px;">
                (Configurable via environment variables: <code>HOTEL_ADMIN_USER</code> &amp; <code>HOTEL_ADMIN_PASS</code>)
            </small>
        </div>

        <form action="${pageContext.request.contextPath}/admin/login" method="POST">
            <!-- CSRF Protection -->
            <input type="hidden" name="_csrf" value="${csrfToken}">

            <div class="form-group" style="margin-bottom: 18px;">
                <label for="username" class="form-label">Administrator Username</label>
                <input type="text" id="username" name="username" class="form-control" 
                       placeholder="Enter admin username" 
                       value="<c:out value='${not empty enteredUsername ? enteredUsername : (enteredUsername != null ? \"\" : \"admin\")}' />" 
                       autocomplete="username">
            </div>

            <div class="form-group" style="margin-bottom: 26px;">
                <label for="password" class="form-label">Password</label>
                <input type="password" id="password" name="password" class="form-control" 
                       placeholder="Enter admin password" 
                       autocomplete="current-password">
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg">
                Authenticate &amp; Access Dashboard
            </button>
        </form>
    </div>
</main>

<jsp:include page="common/footer.jsp" />
