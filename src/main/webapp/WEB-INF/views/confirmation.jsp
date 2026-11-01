<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/nav.jsp" />

<main class="section">
    <div class="container container-narrow">
        <!-- Success Banner -->
        <div style="text-align: center; margin-bottom: 32px;" class="no-print">
            <div style="width: 72px; height: 72px; border-radius: 50%; background-color: var(--success-bg); color: var(--success); display: flex; align-items: center; justify-content: center; font-size: 2.2rem; margin: 0 auto 16px auto;">
                &#10003;
            </div>
            <span class="badge badge-gold" style="margin-bottom: 8px;">Reservation Confirmed</span>
            <h1 class="section-title">Thank You, <c:out value="${reservation.customerName}" />!</h1>
            <p class="section-desc">
                Your reservation at KG Grand Hotel has been successfully confirmed and stored.
            </p>
        </div>

        <!-- Printable Receipt / Voucher -->
        <div class="receipt-card">
            <div class="receipt-header">
                <div class="brand-wrap" style="justify-content: center; margin-bottom: 8px;">
                    <div class="brand-mark">KG</div>
                    <div>
                        <span class="brand-title">KG Grand Hotel</span>
                        <span class="brand-tagline">Luxury &amp; Suites</span>
                    </div>
                </div>
                <p style="font-size: 0.85rem; color: var(--slate);">Official Stay Reservation Voucher &amp; Confirmation Receipt</p>
                <div style="margin-top: 14px;">
                    <span class="badge badge-navy" style="font-size: 0.95rem; padding: 6px 16px;">
                        Reference: <c:out value="${reservation.bookingId}" />
                    </span>
                    <span class="badge ${reservation.status.cssClass}" style="margin-left: 8px;">
                        <c:out value="${reservation.status.displayName}" />
                    </span>
                </div>
            </div>

            <div class="receipt-grid">
                <div class="receipt-item">
                    <label>Guest Full Name</label>
                    <span><c:out value="${reservation.customerName}" /></span>
                </div>

                <div class="receipt-item">
                    <label>Email Address</label>
                    <span><c:out value="${reservation.email}" /></span>
                </div>

                <div class="receipt-item">
                    <label>Phone Number</label>
                    <span><c:out value="${reservation.phone}" /></span>
                </div>

                <div class="receipt-item">
                    <label>Reserved Room</label>
                    <span>
                        <c:choose>
                            <c:when test="${not empty reservation.room}">
                                <c:out value="${reservation.room.name}" /> (<c:out value="${reservation.room.typeDisplayName}" />)
                            </c:when>
                            <c:otherwise>
                                Room #<c:out value="${reservation.roomId}" />
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <div class="receipt-item">
                    <label>Check-In Date</label>
                    <span><c:out value="${reservation.formattedCheckIn}" /> <small style="color:var(--slate);">(from 2:00 PM)</small></span>
                </div>

                <div class="receipt-item">
                    <label>Check-Out Date</label>
                    <span><c:out value="${reservation.formattedCheckOut}" /> <small style="color:var(--slate);">(until 12:00 PM)</small></span>
                </div>

                <div class="receipt-item">
                    <label>Length of Stay</label>
                    <span><c:out value="${reservation.numberOfNights}" /> night<c:if test="${reservation.numberOfNights != 1}">s</c:if></span>
                </div>

                <div class="receipt-item">
                    <label>Number of Guests</label>
                    <span><c:out value="${reservation.guests}" /> Guest<c:if test="${reservation.guests != 1}">s</c:if></span>
                </div>

                <div class="receipt-item">
                    <label>Booking Created</label>
                    <span><c:out value="${reservation.formattedCreatedAt}" /></span>
                </div>

                <div class="receipt-item">
                    <label>Rate Per Night</label>
                    <span><c:out value="${reservation.formattedPricePerNight}" /></span>
                </div>

                <c:if test="${not empty reservation.specialRequests}">
                    <div class="receipt-item" style="grid-column: 1 / -1;">
                        <label>Special Requests</label>
                        <span style="font-style: italic; color: var(--slate-dark);"><c:out value="${reservation.specialRequests}" /></span>
                    </div>
                </c:if>
            </div>

            <!-- Total Price Section -->
            <div class="receipt-total">
                <div>
                    <span style="font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.05em; font-weight: 600; color: var(--slate-dark); display: block;">Total Amount</span>
                    <span style="font-size: 0.8rem; color: var(--slate);">Inclusive of all applicable hotel taxes</span>
                </div>
                <div class="amount">
                    <c:out value="${reservation.formattedTotalPrice}" />
                </div>
            </div>

            <!-- Notice -->
            <div style="font-size: 0.8rem; color: var(--slate); border-top: 1px solid var(--border-color); padding-top: 16px;">
                <strong>Payment Instructions:</strong> This is a guaranteed hotel reservation confirmation. 
                Payment is payable in full at the hotel reception upon check-in via Cash, UPI, Credit Card, or Debit Card.
            </div>
        </div>

        <!-- Action Buttons -->
        <div style="display: flex; gap: 16px; justify-content: center; flex-wrap: wrap;" class="no-print">
            <button class="btn btn-primary btn-print">
                &#128438; Print Confirmation Receipt
            </button>
            <a href="${pageContext.request.contextPath}/my-bookings?bookingId=${reservation.bookingId}&email=${reservation.email}" class="btn btn-outline">
                Manage Reservation in My Bookings
            </a>
            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline">
                Explore More Rooms
            </a>
        </div>
    </div>
</main>

<jsp:include page="common/footer.jsp" />
