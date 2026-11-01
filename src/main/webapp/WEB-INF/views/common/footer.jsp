<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<footer class="footer">
    <div class="container">
        <div class="footer-grid">
            <div>
                <div class="brand-wrap" style="margin-bottom: 16px;">
                    <div class="brand-mark">KG</div>
                    <div>
                        <span class="brand-title" style="color: #FFF;">KG Grand Hotel</span>
                        <span class="brand-tagline">Luxury &amp; Suites</span>
                    </div>
                </div>
                <p class="footer-text">
                    Embodying timeless hospitality, refined comfort, and exceptional service in the heart of the city.
                    Experience unparalleled luxury with bespoke suites, world-class amenities, and tranquil relaxation.
                </p>
                <div style="font-size: 0.8rem; color: #94A3B8;">
                    <strong>Academic Project:</strong> Java Servlet &amp; JSP Web Application (Tomcat 10.1 + XML Data Store)
                </div>
            </div>

            <div>
                <h4 class="footer-title">Quick Links</h4>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/rooms">Our Rooms &amp; Suites</a></li>
                    <li><a href="${pageContext.request.contextPath}/my-bookings">Find My Booking</a></li>
                    <li><a href="${pageContext.request.contextPath}/booking/new">Reserve a Room</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/login">Admin Management</a></li>
                </ul>
            </div>

            <div>
                <h4 class="footer-title">Room Suites</h4>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/rooms?type=STANDARD">Standard Rooms</a></li>
                    <li><a href="${pageContext.request.contextPath}/rooms?type=DELUXE">Deluxe Rooms</a></li>
                    <li><a href="${pageContext.request.contextPath}/rooms?type=SUPERIOR">Superior Rooms</a></li>
                    <li><a href="${pageContext.request.contextPath}/rooms?type=EXECUTIVE_SUITE">Executive Suites</a></li>
                    <li><a href="${pageContext.request.contextPath}/rooms?type=FAMILY_SUITE">Family Suites</a></li>
                    <li><a href="${pageContext.request.contextPath}/rooms?type=PRESIDENTIAL_SUITE">Presidential Suite</a></li>
                </ul>
            </div>

            <div id="contact">
                <h4 class="footer-title">Concierge &amp; Contact</h4>
                <p class="footer-text">
                    KG Grand Boulevard, Civil Lines<br>
                    New Delhi, India - 110001
                </p>
                <p class="footer-text">
                    <strong>Reservations:</strong> +91 (11) 4567-8900<br>
                    <strong>Email:</strong> reservations@kggrandhotel.com<br>
                    <strong>Front Desk:</strong> Available 24 Hours
                </p>
            </div>
        </div>

        <div class="footer-bottom">
            <div>
                &copy; 2026 KG Grand Hotel &amp; Suites. All rights reserved. Built for College Project Demonstration.
            </div>
            <div>
                Built with Java Servlets, JSP &amp; XML Storage
            </div>
        </div>
    </div>
</footer>

<!-- Scripts -->
<script src="${pageContext.request.contextPath}/assets/js/main.js?v=1.0.1"></script>
</body>
</html>
