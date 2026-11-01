package com.hotel.servlet;

import com.hotel.model.Room;
import com.hotel.service.RoomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the luxury home landing page.
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    private final RoomService roomService = RoomService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Room> activeRooms = roomService.getActiveRooms();
        // Showcase up to 3 featured rooms on the homepage
        List<Room> featuredRooms = activeRooms.stream().limit(3).collect(Collectors.toList());

        request.setAttribute("featuredRooms", featuredRooms);
        request.setAttribute("pageTitle", "Luxury Stays & Suites | KG Grand Hotel");

        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
}
