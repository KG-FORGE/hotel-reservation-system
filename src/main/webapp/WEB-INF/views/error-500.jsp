<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/nav.jsp" />

<main class="section">
    <div class="container container-narrow">
        <div class="empty-state" style="padding: 72px 24px;">
            <div style="font-size: 3.5rem; color: var(--error); margin-bottom: 16px;">
                &#9888;
            </div>
            <h1 style="font-size: 2rem; margin-bottom: 12px;">Unexpected Interruption</h1>
            <p style="color: var(--slate-dark); font-size: 1.05rem; max-width: 540px; margin: 0 auto 28px auto;">
                <c:choose>
                    <c:when test="${not empty errorMessage}">
                        <c:out value="${errorMessage}" />
                    </c:when>
                    <c:otherwise>
                        We experienced an unexpected processing condition. Our system administrators have logged this incident. Please refresh or retry shortly.
                    </c:otherwise>
                </c:choose>
            </p>

            <div style="display: flex; gap: 14px; justify-content: center; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/" class="btn btn-gold">
                    Return to Homepage
                </a>
                <a href="${pageContext.request.contextPath}/my-bookings" class="btn btn-outline">
                    Find My Booking
                </a>
            </div>
        </div>
    </div>
</main>

<jsp:include page="common/footer.jsp" />
