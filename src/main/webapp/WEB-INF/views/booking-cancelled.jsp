<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/nav.jsp" />

<main class="section">
    <div class="container container-narrow">
        <div class="detail-card" style="text-align: center; padding: 48px 32px; max-width: 620px; margin: 40px auto;">
            <div style="width: 72px; height: 72px; border-radius: 50%; background-color: var(--error-bg); color: var(--error); display: flex; align-items: center; justify-content: center; font-size: 2.2rem; margin: 0 auto 20px auto;">
                &#10005;
            </div>

            <span class="badge badge-danger" style="margin-bottom: 12px; font-size: 0.85rem; padding: 6px 16px;">
                Status: Cancelled
            </span>

            <h1 class="section-title" style="margin-bottom: 12px;">Reservation Cancelled</h1>
            
            <p style="font-size: 1.05rem; color: var(--slate-dark); margin-bottom: 24px;">
                Booking reference <strong>#<c:out value="${reservation.bookingId}" /></strong> has been officially cancelled.
            </p>

            <div style="background-color: var(--ivory); border-radius: var(--radius-sm); border: 1px solid var(--border-color); padding: 20px; margin-bottom: 32px; text-align: left; font-size: 0.9rem;">
                <div style="margin-bottom: 8px;">
                    <strong>Guest:</strong> <c:out value="${reservation.customerName}" /> (<c:out value="${reservation.email}" />)
                </div>
                <div style="margin-bottom: 8px;">
                    <strong>Original Dates:</strong> <c:out value="${reservation.formattedCheckIn}" /> to <c:out value="${reservation.formattedCheckOut}" />
                </div>
                <div>
                    <strong>Room:</strong> 
                    <c:choose>
                        <c:when test="${not empty reservation.room}">
                            <c:out value="${reservation.room.name}" />
                        </c:when>
                        <c:otherwise>
                            #<c:out value="${reservation.roomId}" />
                        </c:otherwise>
                    </c:choose>
                    <span style="color: var(--success); font-weight: 600;">(Released back to hotel inventory)</span>
                </div>
            </div>

            <div style="display: flex; gap: 14px; justify-content: center; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-gold">
                    Reserve Another Room
                </a>
                <a href="${pageContext.request.contextPath}/" class="btn btn-outline">
                    Return to Home
                </a>
            </div>
        </div>
    </div>
</main>

<jsp:include page="common/footer.jsp" />
