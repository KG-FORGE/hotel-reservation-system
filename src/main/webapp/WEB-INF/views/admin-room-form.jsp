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
            <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-outline btn-sm">
                &larr; Back to Rooms
            </a>
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
            <li class="admin-nav-item">
                <a href="${pageContext.request.contextPath}/admin/reservations">
                    &#128214; All Reservations
                </a>
            </li>
        </ul>
    </aside>

    <main class="admin-main">
        <div class="admin-header">
            <div>
                <h1 style="font-size: 2rem;"><c:out value="${isEdit ? 'Edit Room Record' : 'Add New Room'}" /></h1>
                <p style="color: var(--slate);">Configure room properties, amenities, pricing, and active status.</p>
            </div>
        </div>

        <jsp:include page="common/alerts.jsp" />

        <div class="detail-card" style="max-width: 760px;">
            <form action="${pageContext.request.contextPath}/admin/rooms" method="POST">
                <input type="hidden" name="_csrf" value="${csrfToken}">
                <input type="hidden" name="action" value="save">
                <input type="hidden" name="isEdit" value="${isEdit}">

                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 20px; margin-bottom: 24px;">
                    <!-- Room ID -->
                    <div class="form-group">
                        <label for="id" class="form-label">Room ID *</label>
                        <input type="text" id="id" name="id" class="form-control" 
                               placeholder="e.g. R007" 
                               value="<c:out value='${room.id}' />" 
                               ${isEdit ? 'readonly style="background-color:#F2F2F2;"' : 'required'}>
                        <small style="color: var(--slate); font-size: 0.75rem;">Unique room identifier in XML storage.</small>
                    </div>

                    <!-- Room Name -->
                    <div class="form-group">
                        <label for="name" class="form-label">Room Name *</label>
                        <input type="text" id="name" name="name" class="form-control" 
                               placeholder="e.g. Deluxe Royal Suite" 
                               value="<c:out value='${room.name}' />" 
                               required>
                    </div>

                    <!-- Category Type -->
                    <div class="form-group">
                        <label for="type" class="form-label">Category *</label>
                        <select id="type" name="type" class="form-control" required>
                            <c:forEach var="t" items="${roomTypes}">
                                <option value="${t.name()}" ${room.type == t.name() ? 'selected' : ''}>
                                    <c:out value="${t.displayName}" />
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- Price per night -->
                    <div class="form-group">
                        <label for="pricePerNight" class="form-label">Price per Night (₹ INR) *</label>
                        <input type="number" step="0.01" id="pricePerNight" name="pricePerNight" class="form-control" 
                               placeholder="e.g. 3500.00" 
                               value="<c:out value='${room.pricePerNight}' />" 
                               required>
                    </div>

                    <!-- Guest Capacity -->
                    <div class="form-group">
                        <label for="capacity" class="form-label">Maximum Guests *</label>
                        <input type="number" id="capacity" name="capacity" class="form-control" min="1" max="12" 
                               value="<c:out value='${room.capacity > 0 ? room.capacity : 2}' />" 
                               required>
                    </div>

                    <!-- Image URL -->
                    <div class="form-group">
                        <label for="imageUrl" class="form-label">Image URL</label>
                        <input type="url" id="imageUrl" name="imageUrl" class="form-control" 
                               placeholder="https://images.unsplash.com/..." 
                               value="<c:out value='${room.imageUrl}' />">
                    </div>

                    <!-- Description -->
                    <div class="form-group" style="grid-column: 1 / -1;">
                        <label for="description" class="form-label">Room Description</label>
                        <textarea id="description" name="description" class="form-control" rows="3" 
                                  placeholder="Provide descriptive details highlighting features and views..."><c:out value="${room.description}" /></textarea>
                    </div>

                    <!-- Amenities -->
                    <div class="form-group" style="grid-column: 1 / -1;">
                        <label for="amenities" class="form-label">Amenities (Comma-separated)</label>
                        <input type="text" id="amenities" name="amenities" class="form-control" 
                               placeholder="e.g. Free Wi-Fi, King Bed, Jacuzzi, Balcony, Mini Fridge" 
                               value="<c:out value='${room.amenitiesDisplay}' />">
                        <small style="color: var(--slate); font-size: 0.75rem;">Separate distinct amenities with commas.</small>
                    </div>

                    <!-- Active Status Checkbox -->
                    <div style="grid-column: 1 / -1; display: flex; align-items: center; gap: 10px;">
                        <input type="checkbox" id="active" name="active" value="true" ${room.active ? 'checked' : ''} style="width: 18px; height: 18px;">
                        <label for="active" style="font-weight: 600; font-size: 0.95rem; color: var(--navy); cursor: pointer;">
                            Room is active and open for guest reservations
                        </label>
                    </div>
                </div>

                <div style="display: flex; gap: 14px;">
                    <button type="submit" class="btn btn-gold btn-lg">
                        Save Room Record
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-outline btn-lg">
                        Cancel
                    </a>
                </div>
            </form>
        </div>
    </main>
</div>

<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
