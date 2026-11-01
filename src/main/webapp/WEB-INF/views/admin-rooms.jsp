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
            <li class="admin-nav-item active">
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
    </aside>

    <main class="admin-main">
        <div class="admin-header">
            <div>
                <h1 style="font-size: 2rem;">Manage Room Catalog</h1>
                <p style="color: var(--slate);">Add, modify, or toggle room availability stored in data/rooms.xml</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/rooms?action=new" class="btn btn-gold btn-sm">
                    &#10010; Add New Room
                </a>
            </div>
        </div>

        <jsp:include page="common/alerts.jsp" />

        <div class="table-card">
            <div class="table-responsive">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>Room ID</th>
                            <th>Photo</th>
                            <th>Room Name</th>
                            <th>Category</th>
                            <th>Price / Night</th>
                            <th>Capacity</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="room" items="${rooms}">
                            <tr>
                                <td><strong><c:out value="${room.id}" /></strong></td>
                                <td>
                                    <img src="${room.imageUrl}" alt="${room.name}" 
                                         style="width: 56px; height: 42px; object-fit: cover; border-radius: 4px; border: 1px solid var(--border-color);">
                                </td>
                                <td>
                                    <strong><c:out value="${room.name}" /></strong><br>
                                    <small style="color: var(--slate); max-width: 250px; display: inline-block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                        <c:out value="${room.description}" />
                                    </small>
                                </td>
                                <td><span class="badge badge-gold"><c:out value="${room.typeDisplayName}" /></span></td>
                                <td><strong><c:out value="${room.formattedPrice}" /></strong></td>
                                <td><c:out value="${room.capacity}" /> Guests</td>
                                <td>
                                    <span class="badge ${room.active ? 'badge-success' : 'badge-danger'}">
                                        ${room.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td>
                                    <div style="display: flex; gap: 8px;">
                                        <a href="${pageContext.request.contextPath}/admin/rooms?action=edit&id=${room.id}" class="btn btn-outline btn-sm">
                                            Edit
                                        </a>
                                        <form action="${pageContext.request.contextPath}/admin/rooms" method="POST" style="margin: 0;">
                                            <input type="hidden" name="_csrf" value="${csrfToken}">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="id" value="${room.id}">
                                            <button type="submit" class="btn ${room.active ? 'btn-outline-gold' : 'btn-primary'} btn-sm">
                                                ${room.active ? 'Disable' : 'Enable'}
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
