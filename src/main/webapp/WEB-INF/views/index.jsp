<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/nav.jsp" />

<main>
    <!-- Hero Section -->
    <section class="hero">
        <img src="https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1920&q=80" 
             alt="KG Grand Hotel Exterior & Lobby" 
             class="hero-bg">
        <div class="hero-overlay"></div>
        <div class="container hero-content">
            <span class="hero-badge">Welcome to Pure Luxury</span>
            <h1 class="hero-title">Your Perfect Stay Begins Here.</h1>
            <p class="hero-subtitle">
                Immerse yourself in timeless elegance, exquisite gastronomy, and personalized hospitality at KG Grand Hotel. 
                Where every detail is crafted for your utmost relaxation.
            </p>
            <div style="display: flex; gap: 16px; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-gold btn-lg">Explore All Rooms</a>
                <a href="#about" class="btn btn-outline btn-lg" style="color:#FFF; border-color:#FFF;">Discover Our Story</a>
            </div>
        </div>
    </section>

    <!-- Search Form Bar -->
    <div class="container">
        <div class="search-card">
            <form action="${pageContext.request.contextPath}/rooms" method="GET" class="search-form">
                <div class="form-group">
                    <label for="checkIn" class="form-label">Check-In Date</label>
                    <input type="date" id="checkIn" name="checkIn" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="checkOut" class="form-label">Check-Out Date</label>
                    <input type="date" id="checkOut" name="checkOut" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="capacity" class="form-label">Guests</label>
                    <select id="capacity" name="capacity" class="form-control">
                        <option value="1">1 Guest</option>
                        <option value="2" selected>2 Guests</option>
                        <option value="3">3 Guests</option>
                        <option value="4">4 Guests</option>
                        <option value="6">5+ Guests</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="type" class="form-label">Room Category</label>
                    <select id="type" name="type" class="form-control">
                        <option value="ALL">All Categories</option>
                        <option value="STANDARD">Standard Room</option>
                        <option value="DELUXE">Deluxe Room</option>
                        <option value="SUPERIOR">Superior Room</option>
                        <option value="EXECUTIVE_SUITE">Executive Suite</option>
                        <option value="FAMILY_SUITE">Family Suite</option>
                        <option value="PRESIDENTIAL_SUITE">Presidential Suite</option>
                    </select>
                </div>
                <div>
                    <button type="submit" class="btn btn-primary" style="height: 48px; width: 100%;">
                        Check Availability
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Featured Rooms Section -->
    <section class="section">
        <div class="container">
            <div class="section-header">
                <span class="section-eyebrow">Accommodations</span>
                <h2 class="section-title">Featured Suites &amp; Rooms</h2>
                <p class="section-desc">
                    Each space is an architectural haven designed with natural textures, artisanal touches, and modern state-of-the-art luxuries.
                </p>
            </div>

            <div class="rooms-grid">
                <c:forEach var="room" items="${featuredRooms}">
                    <article class="room-card">
                        <div class="room-media">
                            <img src="${room.imageUrl}" alt="${room.name}" class="room-img" loading="lazy">
                            <span class="badge badge-gold room-badge"><c:out value="${room.typeDisplayName}" /></span>
                            <div class="room-price-badge">
                                <span><c:out value="${room.formattedPrice}" /></span> / night
                            </div>
                        </div>
                        <div class="room-body">
                            <span class="room-category"><c:out value="${room.typeDisplayName}" /></span>
                            <h3 class="room-name"><c:out value="${room.name}" /></h3>
                            <p class="room-desc"><c:out value="${room.description}" /></p>

                            <div class="room-meta">
                                <div class="room-meta-item">
                                    <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 00-3-3.87m-4-12a4 4 0 010 7.75"></path></svg>
                                    <span>Up to <c:out value="${room.capacity}" /> Guests</span>
                                </div>
                                <div class="room-meta-item">
                                    <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M5 12h14M12 5l7 7-7 7"></path></svg>
                                    <span>Premium Bedding</span>
                                </div>
                            </div>

                            <div class="room-actions">
                                <a href="${pageContext.request.contextPath}/rooms/details?id=${room.id}" class="btn btn-outline btn-sm">
                                    View Details
                                </a>
                                <a href="${pageContext.request.contextPath}/booking/new?roomId=${room.id}" class="btn btn-gold btn-sm">
                                    Book Now
                                </a>
                            </div>
                        </div>
                    </article>
                </c:forEach>
            </div>

            <div style="text-align: center; margin-top: 48px;">
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline btn-lg">
                    Browse All Available Rooms &rarr;
                </a>
            </div>
        </div>
    </section>

    <!-- Amenities Section -->
    <section class="section section-alt">
        <div class="container">
            <div class="section-header">
                <span class="section-eyebrow">World-Class Facilities</span>
                <h2 class="section-title">Designed for Your Comfort</h2>
                <p class="section-desc">
                    Immerse yourself in tailored wellness, sublime cuisines, and curated hospitality experiences.
                </p>
            </div>

            <div class="amenities-grid">
                <div class="amenity-card">
                    <div class="amenity-icon">&#127860;</div>
                    <h3 class="amenity-title">Fine Dining &amp; Bar</h3>
                    <p class="amenity-desc">Award-winning executive chefs crafting gourmet culinary creations around the clock.</p>
                </div>
                <div class="amenity-card">
                    <div class="amenity-icon">&#127946;</div>
                    <h3 class="amenity-title">Infinity Pool &amp; Spa</h3>
                    <p class="amenity-desc">Rejuvenate your senses with temperature-controlled rooftop pool and Ayurvedic therapies.</p>
                </div>
                <div class="amenity-card">
                    <div class="amenity-icon">&#128246;</div>
                    <h3 class="amenity-title">High-Speed Wi-Fi</h3>
                    <p class="amenity-desc">Uncapped optical fiber internet connectivity across all suites and communal grounds.</p>
                </div>
                <div class="amenity-card">
                    <div class="amenity-icon">&#128664;</div>
                    <h3 class="amenity-title">Chauffeur &amp; Valet</h3>
                    <p class="amenity-desc">Complimentary premium valet parking and executive airport limousine transfers.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- About Section -->
    <section id="about" class="section">
        <div class="container">
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 48px; align-items: center;">
                <div>
                    <span class="section-eyebrow">Our Heritage</span>
                    <h2 class="section-title">A Legacy of Refined Indian &amp; Global Hospitality</h2>
                    <p style="margin-bottom: 20px; font-size: 1.05rem; line-height: 1.8;">
                        Founded on principles of impeccable graciousness, KG Grand Hotel seamlessly bridges regal Indian heritage 
                        with contemporary architectural precision. From hand-woven textiles to bespoke concierge attention, our mission 
                        is to transform every moment of your stay into a cherished memory.
                    </p>
                    <p style="margin-bottom: 32px; font-size: 0.95rem; color: var(--slate);">
                        Whether you are traveling for executive engagements, peaceful familial retreat, or romantic getaway, our 
                        six signature categories ensure the ideal setting for your aspirations.
                    </p>
                    <a href="${pageContext.request.contextPath}/rooms" class="btn btn-primary">Discover Accommodations</a>
                </div>
                <div>
                    <img src="https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=1000&q=80" 
                         alt="Hotel Architecture and Grounds" 
                         style="width: 100%; border-radius: var(--radius-md); box-shadow: var(--shadow-md); object-fit: cover; height: 420px;">
                </div>
            </div>
        </div>
    </section>

    <!-- Illustrative Guest Testimonials -->
    <section class="section section-alt">
        <div class="container">
            <div class="section-header">
                <span class="section-eyebrow">Guest Stories</span>
                <h2 class="section-title">What Our Guests Say</h2>
                <p class="section-desc">Sample illustrative guest testimonials reflecting authentic stay experiences.</p>
            </div>

            <div class="testimonials-grid">
                <div class="testimonial-card">
                    <p class="testimonial-quote">
                        &ldquo;The Presidential Suite exceeded all expectations. The level of bespoke butler service and panoramic terrace view was truly sublime.&rdquo;
                    </p>
                    <div class="testimonial-author">
                        <div class="testimonial-avatar">AK</div>
                        <div>
                            <div class="testimonial-name">Aarav Kapoor</div>
                            <div class="testimonial-role">Executive Traveler (Sample Guest)</div>
                        </div>
                    </div>
                </div>

                <div class="testimonial-card">
                    <p class="testimonial-quote">
                        &ldquo;From instantaneous online booking to the seamless check-in, KG Grand Hotel defines modern convenience and lavish tranquility.&rdquo;
                    </p>
                    <div class="testimonial-author">
                        <div class="testimonial-avatar">PM</div>
                        <div>
                            <div class="testimonial-name">Priya Menon</div>
                            <div class="testimonial-role">Family Vacationer (Sample Guest)</div>
                        </div>
                    </div>
                </div>

                <div class="testimonial-card">
                    <p class="testimonial-quote">
                        &ldquo;The Deluxe Room was immaculate, serene, and beautifully appointed. The breakfast spread and spa were unforgettable.&rdquo;
                    </p>
                    <div class="testimonial-author">
                        <div class="testimonial-avatar">RS</div>
                        <div>
                            <div class="testimonial-name">Rohan Sharma</div>
                            <div class="testimonial-role">Weekend Traveler (Sample Guest)</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>

<script>
    // Ensure search bar date validation
    document.addEventListener('DOMContentLoaded', function() {
        const checkIn = document.getElementById('checkIn');
        const checkOut = document.getElementById('checkOut');
        if (checkIn && checkOut) {
            const today = new Date().toISOString().split('T')[0];
            checkIn.min = today;
            checkIn.value = today;
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 2);
            checkOut.min = today;
            checkOut.value = tomorrow.toISOString().split('T')[0];
            checkIn.addEventListener('change', function() {
                if (this.value) {
                    checkOut.min = this.value;
                    if (checkOut.value <= this.value) {
                        const next = new Date(this.value);
                        next.setDate(next.getDate() + 1);
                        checkOut.value = next.toISOString().split('T')[0];
                    }
                }
            });
        }
    });
</script>

<jsp:include page="common/footer.jsp" />
