package com.hotel.servlet;

import com.hotel.model.Reservation;
import com.hotel.service.ReservationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for retrieving customer booking records by reference ID and email.
 */
@WebServlet(name = "BookingLookupServlet", urlPatterns = "/my-bookings")
public class BookingLookupServlet extends HttpServlet {

    private final ReservationService reservationService = ReservationService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookingId = request.getParameter("bookingId");
        String email = request.getParameter("email");

        if (bookingId != null && !bookingId.trim().isEmpty() &&
            email != null && !email.trim().isEmpty()) {

            Optional<Reservation> resOpt = reservationService.lookupReservation(bookingId.trim(), email.trim());

            if (resOpt.isPresent()) {
                request.setAttribute("reservation", resOpt.get());
                request.setAttribute("pageTitle", "Booking Details #" + bookingId + " | KG Grand Hotel");
                request.getRequestDispatcher("/WEB-INF/views/booking-details.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("errorMessage", "No active booking found matching Reference ID '" +
                        bookingId + "' and Email '" + email + "'. Please check your credentials and try again.");
                request.setAttribute("searchedBookingId", bookingId);
                request.setAttribute("searchedEmail", email);
            }
        }

        request.setAttribute("pageTitle", "Find My Booking | KG Grand Hotel");
        request.getRequestDispatcher("/WEB-INF/views/my-bookings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
