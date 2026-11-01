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
                Admin: <strong><c:out value="${sessionScope.adminUser.username}" /></strong>
            </span>
            <form action="${pageContext.request.contextPath}/admin/logout" method="POST" style="margin: 0;">
                <input type="hidden" name="_csrf" value="${csrfToken}">
                <button type="submit" class="btn btn-outline-gold btn-sm">Sign Out</button>
            </form>
        </div>
    </div>
</header>

<div class="admin-layout">
    <aside class="admin-sidebar">
        <div style="font-size: 0.8rem; text-transform: uppercase; color: var(--gold); letter-spacing: 0.1em; font-weight: 600;">
            Navigation
        </div>
        <ul class="admin-nav">
            <li class="admin-nav-item">
                <a href="${pageContext.request.contextPath}/admin/dashboard">
                    &#128202; Dashboard Overview
                </a>
            </li>
            <li class="admin-nav-item">
                <a href="${pageContext.request.contextPath}/admin/rooms">
                    &#128719; Manage Rooms
                </a>
            </li>
            <li class="admin-nav-item active">
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
    </aside>

    <main class="admin-main">
        <div class="admin-header">
            <div>
                <h1 style="font-size: 2rem;">Manage Reservations</h1>
                <p style="color: var(--slate);">Review, search, and manage all hotel bookings stored in data/reservations.xml</p>
            </div>
        </div>

        <jsp:include page="common/alerts.jsp" />

        <!-- Search & Filter Filter Form -->
        <div class="search-card" style="margin-top: 0; margin-bottom: 32px;">
            <form action="${pageContext.request.contextPath}/admin/reservations" method="GET" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)) auto; gap: 16px; align-items: flex-end;">
                <div class="form-group">
                    <label for="searchId" class="form-label">Search Keyword</label>
                    <input type="text" id="searchId" name="searchId" class="form-control" 
                           placeholder="Ref ID, Guest Name, or Email" 
                           value="<c:out value='${searchId}' />">
                </div>

                <div class="form-group">
                    <label for="status" class="form-label">Status Filter</label>
                    <select id="status" name="status" class="form-control">
                        <option value="ALL">All Statuses</option>
                        <c:forEach var="s" items="${statuses}">
                            <option value="${s.name()}" ${selectedStatus == s.name() ? 'selected' : ''}>
                                <c:out value="${s.displayName}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="date" class="form-label">Filter Active on Date</label>
                    <input type="date" id="date" name="date" class="form-control" value="<c:out value='${filterDate}' />">
                </div>

                <div style="display: flex; gap: 8px;">
                    <button type="submit" class="btn btn-primary" style="height: 48px;">
                        Filter
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/reservations" class="btn btn-outline" style="height: 48px;">
                        Reset
                    </a>
                </div>
            </form>
        </div>

        <!-- Reservations Table -->
        <div class="table-card">
            <div class="table-header-bar">
                <h2 class="table-header-title">
                    Reservation Records (${reservations.size()})
                </h2>
            </div>

            <div class="table-responsive">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>Ref ID</th>
                            <th>Guest Contact</th>
                            <th>Room</th>
                            <th>Stay Dates</th>
                            <th>Guests</th>
                            <th>Total Amount</th>
                            <th>Status</th>
                            <th>Created At</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty reservations}">
                                <c:forEach var="res" items="${reservations}">
                                    <tr>
                                        <td><strong><c:out value="${res.bookingId}" /></strong></td>
                                        <td>
                                            <strong><c:out value="${res.customerName}" /></strong><br>
                                            <small style="color: var(--slate);"><c:out value="${res.email}" /> &bull; <c:out value="${res.phone}" /></small>
                                            <c:if test="${not empty res.specialRequests}">
                                                <br><small style="color: var(--navy); font-style: italic;">Req: <c:out value="${res.specialRequests}" /></small>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty res.room}">
                                                    <strong><c:out value="${res.room.name}" /></strong><br>
                                                    <small style="color: var(--slate);"><c:out value="${res.room.typeDisplayName}" /></small>
                                                </c:when>
                                                <c:otherwise>
                                                    #<c:out value="${res.roomId}" />
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:out value="${res.formattedCheckIn}" /> &rarr;<br>
                                            <c:out value="${res.formattedCheckOut}" /><br>
                                            <small style="color: var(--slate);">(<c:out value="${res.numberOfNights}" /> nights)</small>
                                        </td>
                                        <td><c:out value="${res.guests}" /></td>
                                        <td><strong><c:out value="${res.formattedTotalPrice}" /></strong></td>
                                        <td>
                                            <span class="badge ${res.status.cssClass}">
                                                <c:out value="${res.status.displayName}" />
                                            </span>
                                        </td>
                                        <td>
                                            <small style="color: var(--slate);"><c:out value="${res.formattedCreatedAt}" /></small>
                                        </td>
                                        <td>
                                            <c:if test="${res.confirmed}">
                                                <form action="${pageContext.request.contextPath}/admin/reservations" method="POST" style="margin: 0;">
                                                    <input type="hidden" name="_csrf" value="${csrfToken}">
                                                    <input type="hidden" name="action" value="cancel">
                                                    <input type="hidden" name="bookingId" value="${res.bookingId}">
                                                    <button type="submit" class="btn btn-danger btn-sm" 
                                                            data-confirm="Are you sure you want to cancel reservation #${res.bookingId} for ${res.customerName}?">
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
                                    <td colspan="9" style="text-align: center; padding: 48px; color: var(--slate);">
                                        No reservations matched the current search or filter criteria.
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

<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
