package com.hotel.servlet;

import com.hotel.model.Reservation;
import com.hotel.service.ReservationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller handling reservation cancellations with email authorization and CSRF validation.
 */
@WebServlet(name = "BookingCancelServlet", urlPatterns = "/booking/cancel")
public class BookingCancelServlet extends HttpServlet {

    private final ReservationService reservationService = ReservationService.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookingId = request.getParameter("bookingId");
        String email = request.getParameter("email");

        try {
            Reservation cancelledRes = reservationService.cancelReservation(bookingId, email);

            request.setAttribute("reservation", cancelledRes);
            request.setAttribute("pageTitle", "Reservation Cancelled #" + cancelledRes.getBookingId() + " | KG Grand Hotel");
            request.getRequestDispatcher("/WEB-INF/views/booking-cancelled.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("bookingId", bookingId);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/my-bookings").forward(request, response);
        }
    }
}
