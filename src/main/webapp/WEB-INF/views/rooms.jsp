<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/nav.jsp" />

<main class="section">
    <div class="container">
        <div class="section-header" style="margin-bottom: 36px;">
            <span class="section-eyebrow">Accommodations &amp; Suites</span>
            <h1 class="section-title">Explore Our Luxury Rooms</h1>
            <p class="section-desc">
                Select your preferred haven of comfort. Filter by room category, guest capacity, price range, or stay dates.
            </p>
        </div>

        <jsp:include page="common/alerts.jsp" />

        <!-- Search & Filter Controls -->
        <div class="search-card" style="margin-top: 0; margin-bottom: 40px;">
            <form action="${pageContext.request.contextPath}/rooms" method="GET">
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; margin-bottom: 16px;">
                    <!-- Keyword Search -->
                    <div class="form-group">
                        <label for="query" class="form-label">Search Keywords</label>
                        <input type="text" id="query" name="query" class="form-control" 
                               placeholder="e.g. Balcony, Jacuzzi, King" 
                               value="<c:out value='${searchQuery}' />">
                    </div>

                    <!-- Category Filter -->
                    <div class="form-group">
                        <label for="type" class="form-label">Room Category</label>
                        <select id="type" name="type" class="form-control">
                            <option value="ALL">All Categories</option>
                            <c:forEach var="t" items="${roomTypes}">
                                <option value="${t.name()}" ${selectedType == t.name() ? 'selected' : ''}>
                                    <c:out value="${t.displayName}" />
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- Max Price -->
                    <div class="form-group">
                        <label for="maxPrice" class="form-label">Max Price / Night</label>
                        <select id="maxPrice" name="maxPrice" class="form-control">
                            <option value="">Any Price</option>
                            <option value="2000" ${selectedMaxPrice == 2000 ? 'selected' : ''}>Under ₹2,000</option>
                            <option value="3500" ${selectedMaxPrice == 3500 ? 'selected' : ''}>Under ₹3,500</option>
                            <option value="5000" ${selectedMaxPrice == 5000 ? 'selected' : ''}>Under ₹5,000</option>
                            <option value="10000" ${selectedMaxPrice == 10000 ? 'selected' : ''}>Under ₹10,000</option>
                        </select>
                    </div>

                    <!-- Minimum Capacity -->
                    <div class="form-group">
                        <label for="capacity" class="form-label">Min Guests</label>
                        <select id="capacity" name="capacity" class="form-control">
                            <option value="">Any Capacity</option>
                            <option value="1" ${selectedCapacity == 1 ? 'selected' : ''}>1+ Guest</option>
                            <option value="2" ${selectedCapacity == 2 ? 'selected' : ''}>2+ Guests</option>
                            <option value="3" ${selectedCapacity == 3 ? 'selected' : ''}>3+ Guests</option>
                            <option value="4" ${selectedCapacity == 4 ? 'selected' : ''}>4+ Guests</option>
                            <option value="6" ${selectedCapacity == 6 ? 'selected' : ''}>6 Guests</option>
                        </select>
                    </div>

                    <!-- Sort By -->
                    <div class="form-group">
                        <label for="sortBy" class="form-label">Sort By</label>
                        <select id="sortBy" name="sortBy" class="form-control">
                            <option value="">Recommended</option>
                            <option value="price_asc" ${selectedSortBy == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                            <option value="price_desc" ${selectedSortBy == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                            <option value="capacity_desc" ${selectedSortBy == 'capacity_desc' ? 'selected' : ''}>Capacity: Largest First</option>
                            <option value="name_asc" ${selectedSortBy == 'name_asc' ? 'selected' : ''}>Room Name: A to Z</option>
                        </select>
                    </div>
                </div>

                <!-- Date Range Filter Row -->
                <div style="display: flex; gap: 16px; flex-wrap: wrap; align-items: flex-end; padding-top: 14px; border-top: 1px solid var(--slate-light);">
                    <div class="form-group" style="flex: 1; min-width: 160px;">
                        <label for="filterCheckIn" class="form-label">Filter Check-In</label>
                        <input type="date" id="filterCheckIn" name="checkIn" class="form-control" value="<c:out value='${checkIn}' />">
                    </div>
                    <div class="form-group" style="flex: 1; min-width: 160px;">
                        <label for="filterCheckOut" class="form-label">Filter Check-Out</label>
                        <input type="date" id="filterCheckOut" name="checkOut" class="form-control" value="<c:out value='${checkOut}' />">
                    </div>
                    <div style="display: flex; gap: 8px;">
                        <button type="submit" class="btn btn-primary" style="height: 48px;">
                            Apply Filters
                        </button>
                        <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline" style="height: 48px;">
                            Reset
                        </a>
                    </div>
                </div>
            </form>
        </div>

        <!-- Room Listing Grid -->
        <c:choose>
            <c:when test="${not empty rooms}">
                <div style="margin-bottom: 24px; font-size: 0.95rem; color: var(--slate-dark);">
                    Showing <strong>${rooms.size()}</strong> luxury accommodation<c:if test="${rooms.size() != 1}">s</c:if>
                    <c:if test="${not empty checkIn && not empty checkOut}">
                        available from <strong><c:out value="${checkIn}" /></strong> to <strong><c:out value="${checkOut}" /></strong>
                    </c:if>
                </div>

                <div class="rooms-grid">
                    <c:forEach var="room" items="${rooms}">
                        <article class="room-card">
                            <div class="room-media">
                                <img src="${room.imageUrl}" alt="${room.name}" class="room-img" loading="lazy">
                                <span class="badge badge-gold room-badge">#<c:out value="${room.id}" /> &bull; <c:out value="${room.typeDisplayName}" /></span>
                                <div class="room-price-badge">
                                    <span><c:out value="${room.formattedPrice}" /></span> / night
                                </div>
                            </div>

                            <div class="room-body">
                                <span class="room-category"><c:out value="${room.typeDisplayName}" /></span>
                                <h2 class="room-name"><c:out value="${room.name}" /></h2>
                                <p class="room-desc"><c:out value="${room.description}" /></p>

                                <div class="amenities-tag-list">
                                    <c:forEach var="amenity" items="${room.amenities}" varStatus="loop">
                                        <c:if test="${loop.index < 4}">
                                            <span class="amenity-tag"><c:out value="${amenity}" /></span>
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${room.amenities.size() > 4}">
                                        <span class="amenity-tag">+${room.amenities.size() - 4} more</span>
                                    </c:if>
                                </div>

                                <div class="room-meta">
                                    <div class="room-meta-item">
                                        <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 00-3-3.87m-4-12a4 4 0 010 7.75"></path></svg>
                                        <span>Max <c:out value="${room.capacity}" /> Guests</span>
                                    </div>
                                    <div class="room-meta-item">
                                        <span class="badge ${room.active ? 'badge-success' : 'badge-danger'}">
                                            ${room.active ? 'Available' : 'Unavailable'}
                                        </span>
                                    </div>
                                </div>

                                <div class="room-actions">
                                    <a href="${pageContext.request.contextPath}/rooms/details?id=${room.id}&checkIn=${checkIn}&checkOut=${checkOut}&guests=${selectedCapacity}" 
                                       class="btn btn-outline btn-sm">
                                        View Details
                                    </a>
                                    <c:choose>
                                        <c:when test="${room.active}">
                                            <a href="${pageContext.request.contextPath}/booking/new?roomId=${room.id}&checkIn=${checkIn}&checkOut=${checkOut}&guests=${selectedCapacity}" 
                                               class="btn btn-gold btn-sm">
                                                Book Now
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="btn btn-outline btn-sm" disabled style="opacity: 0.6; cursor: not-allowed;">
                                                Unavailable
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">&#128719;</div>
                    <h3 style="margin-bottom: 8px;">No Rooms Found</h3>
                    <p style="margin-bottom: 24px; color: var(--slate);">
                        No accommodations matched your filter criteria or selected dates. Try adjusting your dates, category, or search keywords.
                    </p>
                    <a href="${pageContext.request.contextPath}/rooms" class="btn btn-gold">
                        Clear All Filters
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="common/footer.jsp" />
