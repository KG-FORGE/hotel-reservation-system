<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">

<header class="navbar">
    <div class="container nav-container">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="brand-wrap">
            <div class="brand-mark">KG</div>
            <div>
                <span class="brand-title">KG Grand Hotel</span>
                <span class="brand-tagline">Administration Suite</span>
            </div>
        </a>
        <div class="nav-actions">
            <span style="font-size: 0.85rem; color: var(--slate-dark);">
                Logged in as: <strong><c:out value="${sessionScope.adminUser.username}" /></strong>
            </span>
            <a href="${pageContext.request.contextPath}/" target="_blank" class="btn btn-outline btn-sm">
                View Live Website &#8599;
            </a>
            <form action="${pageContext.request.contextPath}/admin/logout" method="POST" style="margin: 0;">
                <input type="hidden" name="_csrf" value="${csrfToken}">
                <button type="submit" class="btn btn-outline-gold btn-sm">Sign Out</button>
            </form>
        </div>
    </div>
</header>

<div class="admin-layout">
    <!-- Admin Sidebar -->
    <aside class="admin-sidebar">
        <div style="font-size: 0.8rem; text-transform: uppercase; color: var(--gold); letter-spacing: 0.1em; font-weight: 600;">
            Navigation
        </div>
        <ul class="admin-nav">
            <li class="admin-nav-item active">
                <a href="${pageContext.request.contextPath}/admin/dashboard">
                    &#128202; Dashboard Overview
                </a>
            </li>
            <li class="admin-nav-item">
                <a href="${pageContext.request.contextPath}/admin/rooms">
                    &#128719; Manage Rooms
                </a>
            </li>
            <li class="admin-nav-item">
                <a href="${pageContext.request.contextPath}/admin/reservations">
                    &#128214; All Reservations
                </a>
            </li>
            <li class="admin-nav-item">
                <a href="${pageContext.request.contextPath}/admin/rooms?action=new">
                    &#10010; Add New Room
                </a>
            </li>
        </ul>

        <div style="margin-top: auto; padding-top: 20px; border-top: 1px solid var(--navy-light); font-size: 0.75rem; color: #94A3B8;">
            <strong>XML Storage Active:</strong><br>
            rooms.xml &amp; reservations.xml
        </div>
    </aside>

    <!-- Admin Main Body -->
    <main class="admin-main">
        <div class="admin-header">
            <div>
                <h1 style="font-size: 2rem;">Executive Dashboard</h1>
                <p style="color: var(--slate);">Real-time hotel room inventory and reservation metrics calculated from XML persistence.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/rooms?action=new" class="btn btn-gold btn-sm">
                    &#10010; Add Room
                </a>
            </div>
        </div>

        <jsp:include page="common/alerts.jsp" />

        <!-- Metrics Cards -->
        <div class="stats-grid">
            <div class="stat-card">
                <span class="stat-label">Total Rooms</span>
                <span class="stat-value"><c:out value="${stats.totalRooms}" /></span>
                <span class="stat-subtext"><c:out value="${stats.activeRooms}" /> currently active for booking</span>
            </div>

            <div class="stat-card">
                <span class="stat-label">Confirmed Bookings</span>
                <span class="stat-value" style="color: var(--success);"><c:out value="${stats.confirmedReservations}" /></span>
                <span class="stat-subtext">Out of <c:out value="${stats.totalReservations}" /> total historical reservations</span>
            </div>

            <div class="stat-card">
                <span class="stat-label">Upcoming Check-Ins</span>
                <span class="stat-value" style="color: var(--gold);"><c:out value="${stats.upcomingCheckIns}" /></span>
                <span class="stat-subtext">Active bookings scheduled for future stay</span>
            </div>

            <div class="stat-card">
                <span class="stat-label">Estimated Revenue</span>
                <span class="stat-value" style="font-size: 1.7rem;"><c:out value="${stats.formattedRevenue}" /></span>
                <span class="stat-subtext">Based on confirmed stay durations</span>
            </div>
        </div>

        <!-- Recent Reservations Table -->
        <div class="table-card">
            <div class="table-header-bar">
                <h2 class="table-header-title">Recent Reservations</h2>
                <a href="${pageContext.request.contextPath}/admin/reservations" class="btn btn-outline btn-sm">
                    View All Reservations &rarr;
                </a>
            </div>

            <div class="table-responsive">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>Ref ID</th>
                            <th>Guest Name</th>
                            <th>Room</th>
                            <th>Check-In</th>
                            <th>Check-Out</th>
                            <th>Guests</th>
                            <th>Total Price</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty recentReservations}">
                                <c:forEach var="res" items="${recentReservations}">
                                    <tr>
                                        <td><strong><c:out value="${res.bookingId}" /></strong></td>
                                        <td>
                                            <c:out value="${res.customerName}" /><br>
                                            <small style="color: var(--slate);"><c:out value="${res.email}" /></small>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty res.room}">
                                                    <c:out value="${res.room.name}" />
                                                </c:when>
                                                <c:otherwise>
                                                    #<c:out value="${res.roomId}" />
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><c:out value="${res.formattedCheckIn}" /></td>
                                        <td><c:out value="${res.formattedCheckOut}" /></td>
                                        <td><c:out value="${res.guests}" /></td>
                                        <td><strong><c:out value="${res.formattedTotalPrice}" /></strong></td>
                                        <td>
                                            <span class="badge ${res.status.cssClass}">
                                                <c:out value="${res.status.displayName}" />
                                            </span>
                                        </td>
                                        <td>
                                            <c:if test="${res.confirmed}">
                                                <form action="${pageContext.request.contextPath}/admin/reservations" method="POST" style="display:inline;">
                                                    <input type="hidden" name="_csrf" value="${csrfToken}">
                                                    <input type="hidden" name="action" value="cancel">
                                                    <input type="hidden" name="bookingId" value="${res.bookingId}">
                                                    <button type="submit" class="btn btn-danger btn-sm" 
                                                            data-confirm="Cancel reservation ${res.bookingId} for guest ${res.customerName}?">
                                                        Cancel
                                                    </button>
                                                </form>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="9" style="text-align: center; padding: 40px; color: var(--slate);">
                                        No reservations found in XML storage yet.
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<!-- Scripts -->
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
