<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/nav.jsp" />

<main class="section">
    <div class="container">
        <div style="margin-bottom: 24px;">
            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline btn-sm">
                &larr; Back to All Rooms
            </a>
        </div>

        <jsp:include page="common/alerts.jsp" />

        <div class="room-detail-hero">
            <!-- Left: Gallery / Large Media -->
            <div class="room-gallery-main">
                <img src="${room.imageUrl}" alt="${room.name}" id="roomMainImage">
            </div>

            <!-- Right: Details Card & Direct Booking -->
            <div class="detail-card">
                <span class="badge badge-gold" style="margin-bottom: 12px;">
                    #<c:out value="${room.id}" /> &bull; <c:out value="${room.typeDisplayName}" />
                </span>
                <h1 style="font-size: 2.2rem; margin-bottom: 12px;"><c:out value="${room.name}" /></h1>

                <div class="detail-price-box">
                    <span class="price"><c:out value="${room.formattedPrice}" /></span>
                    <span style="color: var(--slate); font-size: 0.95rem;"> / per night (taxes included)</span>
                </div>

                <div style="margin-bottom: 24px;">
                    <h3 style="font-size: 1.1rem; margin-bottom: 8px;">About This Room</h3>
                    <p style="font-size: 0.95rem; color: var(--slate-dark); line-height: 1.8;">
                        <c:out value="${room.description}" />
                    </p>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 24px; padding: 16px; background-color: var(--ivory); border-radius: var(--radius-sm);">
                    <div>
                        <span style="font-size: 0.75rem; text-transform: uppercase; color: var(--slate); font-weight: 600; display: block;">Capacity</span>
                        <strong style="color: var(--navy);">Up to ${room.capacity} Guests</strong>
                    </div>
                    <div>
                        <span style="font-size: 0.75rem; text-transform: uppercase; color: var(--slate); font-weight: 600; display: block;">Room Status</span>
                        <strong class="${room.active ? 'badge badge-success' : 'badge badge-danger'}">
                            ${room.active ? 'Accepting Bookings' : 'Currently Inactive'}
                        </strong>
                    </div>
                </div>

                <!-- Date Availability Checker Form -->
                <div style="margin-bottom: 24px; padding: 20px; border: 1px solid var(--border-color); border-radius: var(--radius-sm); background: #FAF9F6;">
                    <h4 style="font-size: 0.95rem; margin-bottom: 12px; text-transform: uppercase; letter-spacing: 0.05em; color: var(--navy);">
                        Check Stay Availability
                    </h4>
                    <form action="${pageContext.request.contextPath}/rooms/details" method="GET" style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                        <input type="hidden" name="id" value="${room.id}">
                        <div class="form-group">
                            <label for="detailIn" class="form-label" style="font-size: 0.75rem;">Check-In</label>
                            <input type="date" id="detailIn" name="checkIn" class="form-control" value="<c:out value='${checkIn}' />" required>
                        </div>
                        <div class="form-group">
                            <label for="detailOut" class="form-label" style="font-size: 0.75rem;">Check-Out</label>
                            <input type="date" id="detailOut" name="checkOut" class="form-control" value="<c:out value='${checkOut}' />" required>
                        </div>
                        <div style="grid-column: span 2;">
                            <button type="submit" class="btn btn-primary btn-block btn-sm">
                                Check Selected Dates
                            </button>
                        </div>
                    </form>

                    <c:if test="${checkedDates}">
                        <div style="margin-top: 14px;">
                            <c:choose>
                                <c:when test="${isAvailable}">
                                    <div class="alert alert-success" style="margin-bottom: 0; padding: 10px 14px; font-size: 0.85rem;">
                                        &check; Room is available for your dates!
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-danger" style="margin-bottom: 0; padding: 10px 14px; font-size: 0.85rem;">
                                        &cross; Room is booked for these dates. Please choose alternate dates.
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </div>

                <!-- Booking Action -->
                <div>
                    <c:choose>
                        <c:when test="${room.active}">
                            <a href="${pageContext.request.contextPath}/booking/new?roomId=${room.id}&checkIn=${checkIn}&checkOut=${checkOut}&guests=${guests}" 
                               class="btn btn-gold btn-lg btn-block">
                                Proceed to Reserve Room &rarr;
                            </a>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-outline btn-lg btn-block" disabled style="opacity: 0.6; cursor: not-allowed;">
                                Room Currently Inactive
                            </button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Room Amenities -->
        <div class="detail-card" style="margin-top: 36px;">
            <h3 style="font-size: 1.4rem; margin-bottom: 20px;">Included Room Amenities &amp; Features</h3>
            <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px;">
                <c:forEach var="amenity" items="${room.amenities}">
                    <div style="display: flex; align-items: center; gap: 10px; padding: 12px; background: var(--ivory); border-radius: var(--radius-sm); border: 1px solid var(--border-color);">
                        <span style="color: var(--gold); font-size: 1.1rem;">&#10003;</span>
                        <span style="font-weight: 500; font-size: 0.9rem; color: var(--navy);"><c:out value="${amenity}" /></span>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</main>

<jsp:include page="common/footer.jsp" />
