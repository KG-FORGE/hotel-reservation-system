<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/nav.jsp" />

<main class="section">
    <div class="container container-narrow">
        <div class="empty-state" style="padding: 80px 24px;">
            <div style="font-size: 4.5rem; font-family: var(--font-serif); color: var(--gold); font-weight: 700; line-height: 1;">
                404
            </div>
            <h1 style="font-size: 2rem; margin: 16px 0 12px 0;">Destination Not Found</h1>
            <p style="color: var(--slate); font-size: 1.05rem; max-width: 500px; margin: 0 auto 32px auto;">
                The page or room you were looking for seems to have moved or does not exist in our system.
            </p>
            <div style="display: flex; gap: 14px; justify-content: center; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/" class="btn btn-gold">
                    Return to Homepage
                </a>
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline">
                    Explore Rooms &amp; Suites
                </a>
            </div>
        </div>
    </div>
</main>

<jsp:include page="common/footer.jsp" />
