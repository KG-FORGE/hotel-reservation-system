<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/nav.jsp" />

<main class="section">
    <div class="container container-narrow">
        <div style="margin-bottom: 24px;" class="no-print">
            <a href="${pageContext.request.contextPath}/my-bookings" class="btn btn-outline btn-sm">
                &larr; Back to Booking Lookup
            </a>
        </div>

        <jsp:include page="common/alerts.jsp" />

        <div class="receipt-card">
            <div class="receipt-header">
                <span class="section-eyebrow">Stay Reservation Summary</span>
                <h1 style="font-size: 2rem; margin-top: 6px; margin-bottom: 12px;">Booking #<c:out value="${reservation.bookingId}" /></h1>
                <div>
                    <span class="badge ${reservation.status.cssClass}" style="font-size: 0.9rem; padding: 6px 16px;">
                        <c:out value="${reservation.status.displayName}" />
                    </span>
                </div>
            </div>

            <div class="receipt-grid">
                <div class="receipt-item">
                    <label>Guest Name</label>
                    <span><c:out value="${reservation.customerName}" /></span>
                </div>

                <div class="receipt-item">
                    <label>Registered Email</label>
                    <span><c:out value="${reservation.email}" /></span>
                </div>

                <div class="receipt-item">
                    <label>Phone Contact</label>
                    <span><c:out value="${reservation.phone}" /></span>
                </div>

                <div class="receipt-item">
                    <label>Reserved Accommodation</label>
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
                    <span><c:out value="${reservation.formattedCheckIn}" /></span>
                </div>

                <div class="receipt-item">
                    <label>Check-Out Date</label>
                    <span><c:out value="${reservation.formattedCheckOut}" /></span>
                </div>

                <div class="receipt-item">
                    <label>Length of Stay</label>
                    <span><c:out value="${reservation.numberOfNights}" /> night<c:if test="${reservation.numberOfNights != 1}">s</c:if></span>
                </div>

                <div class="receipt-item">
                    <label>Guests Accommodated</label>
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
                        <span style="font-style: italic;"><c:out value="${reservation.specialRequests}" /></span>
                    </div>
                </c:if>
            </div>

            <div class="receipt-total">
                <div>
                    <span style="font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.05em; font-weight: 600; color: var(--slate-dark); display: block;">Total Amount</span>
                    <span style="font-size: 0.8rem; color: var(--slate);">Inclusive of all applicable taxes</span>
                </div>
                <div class="amount">
                    <c:out value="${reservation.formattedTotalPrice}" />
                </div>
            </div>

            <!-- Cancellation Section -->
            <div style="border-top: 1px solid var(--border-color); padding-top: 24px; margin-top: 24px;" class="no-print">
                <c:choose>
                    <c:when test="${reservation.confirmed}">
                        <c:choose>
                            <c:when test="${reservation.eligibleForCancellation}">
                                <div style="background-color: #FDF4E6; border: 1px solid #F5D6A7; border-radius: var(--radius-sm); padding: 18px; margin-bottom: 20px;">
                                    <h4 style="color: #925C08; font-size: 0.95rem; margin-bottom: 6px;">Need to cancel your stay?</h4>
                                    <p style="font-size: 0.85rem; color: #784A04; margin-bottom: 14px;">
                                        Cancellations are complimentary when requested prior to the check-in date. Once cancelled, the room is immediately made available for other guests.
                                    </p>

                                    <form action="${pageContext.request.contextPath}/booking/cancel" method="POST" style="display: inline-block;">
                                        <input type="hidden" name="_csrf" value="${csrfToken}">
                                        <input type="hidden" name="bookingId" value="${reservation.bookingId}">
                                        <input type="hidden" name="email" value="${reservation.email}">
                                        <button type="submit" class="btn btn-danger btn-sm" 
                                                data-confirm="Are you sure you want to cancel booking #${reservation.bookingId}? This action is irreversible.">
                                            Cancel This Reservation
                                        </button>
                                    </form>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-info">
                                    This reservation is no longer eligible for online cancellation as the check-in date has commenced. For inquiries, please contact hotel concierge directly.
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-danger">
                            &cross; This reservation has been cancelled. No reservation holds or hotel charges apply.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div style="display: flex; gap: 16px; justify-content: center;" class="no-print">
            <button class="btn btn-primary btn-print">
                &#128438; Print Voucher
            </button>
            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline">
                Browse Rooms
            </a>
        </div>
    </div>
</main>

<jsp:include page="common/footer.jsp" />
