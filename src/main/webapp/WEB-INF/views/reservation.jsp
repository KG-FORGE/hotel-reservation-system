<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/nav.jsp" />

<main class="section">
    <div class="container container-narrow">
        <div class="section-header" style="margin-bottom: 32px;">
            <span class="section-eyebrow">Direct Reservations</span>
            <h1 class="section-title">Complete Your Reservation</h1>
            <p class="section-desc">
                Book directly with KG Grand Hotel for complimentary high-speed Wi-Fi, flexible cancellation, and best rate guarantee.
            </p>
        </div>

        <!-- Server-side alert -->
        <jsp:include page="common/alerts.jsp" />

        <!-- Client-side validation alert box -->
        <div id="clientValidationAlert" class="alert alert-danger" style="display: none;" role="alert"></div>

        <div class="detail-card">
            <form id="reservationForm" action="${pageContext.request.contextPath}/booking/new" method="POST">
                <!-- CSRF Protection Token -->
                <input type="hidden" name="_csrf" value="${csrfToken}">

                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 20px; margin-bottom: 24px;">
                    <!-- Customer Name -->
                    <div class="form-group">
                        <label for="customerName" class="form-label">Full Name *</label>
                        <input type="text" id="customerName" name="customerName" class="form-control" 
                               placeholder="e.g. Vikram Malhotra" 
                               value="<c:out value='${not empty param_customerName ? param_customerName : param.customerName}' />" 
                               required>
                    </div>

                    <!-- Email Address -->
                    <div class="form-group">
                        <label for="email" class="form-label">Email Address *</label>
                        <input type="email" id="email" name="email" class="form-control" 
                               placeholder="e.g. vikram@example.com" 
                               value="<c:out value='${not empty param_email ? param_email : param.email}' />" 
                               required>
                        <small style="color: var(--slate); font-size: 0.75rem;">Your booking confirmation voucher will be linked to this email.</small>
                    </div>

                    <!-- Phone Number -->
                    <div class="form-group">
                        <label for="phone" class="form-label">Contact Phone Number *</label>
                        <input type="tel" id="phone" name="phone" class="form-control" 
                               placeholder="e.g. +91 98765 43210" 
                               value="<c:out value='${not empty param_phone ? param_phone : param.phone}' />" 
                               required>
                    </div>

                    <!-- Room Selection -->
                    <div class="form-group">
                        <label for="roomId" class="form-label">Selected Room Category *</label>
                        <select id="roomId" name="roomId" class="form-control" required>
                            <option value="">-- Choose a Room --</option>
                            <c:forEach var="r" items="${activeRooms}">
                                <c:set var="isSelected" value="${(not empty selectedRoom && selectedRoom.id == r.id) || (param_roomId == r.id) || (param.roomId == r.id)}" />
                                <option value="${r.id}" 
                                        data-price="${r.pricePerNight}" 
                                        data-capacity="${r.capacity}" 
                                        ${isSelected ? 'selected' : ''}>
                                    <c:out value="${r.name}" /> &bull; <c:out value="${r.formattedPrice}" />/night (Max ${r.capacity} Guests)
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- Check-In Date -->
                    <div class="form-group">
                        <label for="checkIn" class="form-label">Check-In Date *</label>
                        <input type="date" id="checkIn" name="checkIn" class="form-control" 
                               value="<c:out value='${defaultCheckIn}' />" 
                               required>
                    </div>

                    <!-- Check-Out Date -->
                    <div class="form-group">
                        <label for="checkOut" class="form-label">Check-Out Date *</label>
                        <input type="date" id="checkOut" name="checkOut" class="form-control" 
                               value="<c:out value='${defaultCheckOut}' />" 
                               required>
                    </div>

                    <!-- Guest Count -->
                    <div class="form-group">
                        <label for="guests" class="form-label">Number of Guests *</label>
                        <input type="number" id="guests" name="guests" class="form-control" min="1" max="10" 
                               value="<c:out value='${defaultGuests}' />" 
                               required>
                    </div>

                    <!-- Special Requests -->
                    <div class="form-group" style="grid-column: 1 / -1;">
                        <label for="specialRequests" class="form-label">Special Requests (Optional)</label>
                        <textarea id="specialRequests" name="specialRequests" class="form-control" rows="3" 
                                  placeholder="e.g. Quiet room requested, early check-in preference, airport pickup details..."><c:out value="${not empty param_specialRequests ? param_specialRequests : param.specialRequests}" /></textarea>
                    </div>
                </div>

                <!-- Live Price Calculation & Summary Box -->
                <div style="background-color: var(--ivory); border-radius: var(--radius-sm); border: 1px solid var(--border-color); padding: 24px; margin-bottom: 28px;">
                    <h3 style="font-size: 1.1rem; margin-bottom: 16px; color: var(--navy);">Reservation Price Summary</h3>
                    
                    <div style="display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 0.95rem;">
                        <span style="color: var(--slate-dark);">Estimated Duration:</span>
                        <strong id="previewNights" style="color: var(--navy);">-- nights</strong>
                    </div>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 14px; font-size: 0.95rem;">
                        <span style="color: var(--slate-dark);">Applicable Taxes &amp; Fees:</span>
                        <span style="color: var(--success); font-weight: 600;">Included in Rate</span>
                    </div>

                    <div style="border-top: 1px dashed var(--border-color); padding-top: 14px; display: flex; justify-content: space-between; align-items: center;">
                        <div>
                            <span style="font-weight: 600; font-size: 1.1rem; color: var(--navy); display: block;">Total Payable:</span>
                            <span style="font-size: 0.8rem; color: var(--slate);">(Pay directly at hotel reception upon check-in)</span>
                        </div>
                        <span id="previewTotal" style="font-size: 1.85rem; font-family: var(--font-serif); font-weight: 700; color: var(--gold);">
                            ₹0.00
                        </span>
                    </div>
                </div>

                <!-- Terms and Submission -->
                <div style="margin-bottom: 24px; font-size: 0.85rem; color: var(--slate); line-height: 1.6;">
                    <strong>Reservation Policy:</strong> Check-in commences at 2:00 PM. Check-out is by 12:00 PM noon. 
                    No upfront online payment is required for this demonstration. Cancellations can be made free of charge prior to the check-in date.
                </div>

                <div style="display: flex; gap: 16px; flex-wrap: wrap;">
                    <button type="submit" class="btn btn-gold btn-lg" style="flex: 1;">
                        Confirm &amp; Place Reservation
                    </button>
                    <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline btn-lg">
                        Cancel
                    </a>
                </div>
            </form>
        </div>
    </div>
</main>

<script src="${pageContext.request.contextPath}/assets/js/validation.js"></script>
<jsp:include page="common/footer.jsp" />
