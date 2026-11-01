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
 * Controller displaying successful booking confirmation and print receipt.
 */
@WebServlet(name = "BookingConfirmationServlet", urlPatterns = "/booking/confirmation")
public class BookingConfirmationServlet extends HttpServlet {

    private final ReservationService reservationService = ReservationService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookingId = request.getParameter("id");
        if (bookingId == null || bookingId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }

        Optional<Reservation> resOpt = reservationService.getReservationById(bookingId.trim());
        if (resOpt.isEmpty()) {
            request.setAttribute("errorMessage", "Booking reference not found: " + bookingId);
            request.getRequestDispatcher("/WEB-INF/views/error-404.jsp").forward(request, response);
            return;
        }

        Reservation reservation = resOpt.get();
        request.setAttribute("reservation", reservation);
        request.setAttribute("pageTitle", "Booking Confirmed #" + reservation.getBookingId() + " | KG Grand Hotel");

        request.getRequestDispatcher("/WEB-INF/views/confirmation.jsp").forward(request, response);
    }
}
