<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/nav.jsp" />

<main class="section">
    <div class="container container-narrow">
        <div class="section-header" style="margin-bottom: 32px;">
            <span class="section-eyebrow">Guest Services</span>
            <h1 class="section-title">Find &amp; Manage Your Booking</h1>
            <p class="section-desc">
                Review your reservation details, print confirmation vouchers, or manage your upcoming stay.
            </p>
        </div>

        <jsp:include page="common/alerts.jsp" />

        <div class="detail-card" style="max-width: 540px; margin: 0 auto;">
            <form action="${pageContext.request.contextPath}/my-bookings" method="GET">
                <div class="form-group" style="margin-bottom: 20px;">
                    <label for="bookingId" class="form-label">Booking Reference Number *</label>
                    <input type="text" id="bookingId" name="bookingId" class="form-control" 
                           placeholder="e.g. BK100001" 
                           value="<c:out value='${searchedBookingId != null ? searchedBookingId : param.bookingId}' />" 
                           required>
                    <small style="color: var(--slate); font-size: 0.75rem;">Found in your confirmation receipt (Format: BK followed by 6 digits)</small>
                </div>

                <div class="form-group" style="margin-bottom: 28px;">
                    <label for="email" class="form-label">Customer Email Address *</label>
                    <input type="email" id="email" name="email" class="form-control" 
                           placeholder="e.g. guest@example.com" 
                           value="<c:out value='${searchedEmail != null ? searchedEmail : param.email}' />" 
                           required>
                    <small style="color: var(--slate); font-size: 0.75rem;">Must match the email entered when placing the reservation</small>
                </div>

                <div style="background-color: var(--ivory); padding: 14px; border-radius: var(--radius-sm); border: 1px solid var(--border-color); margin-bottom: 24px; font-size: 0.8rem; color: var(--slate-dark);">
                    &#128274; <strong>Privacy Notice:</strong> We require both your booking reference and matching email to protect your privacy and prevent unauthorized access.
                </div>

                <button type="submit" class="btn btn-gold btn-lg btn-block">
                    Search Booking Record
                </button>
            </form>
        </div>
    </div>
</main>

<jsp:include page="common/footer.jsp" />
