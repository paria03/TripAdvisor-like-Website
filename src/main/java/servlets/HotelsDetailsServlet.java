package servlets;

import data.Hotel;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.HotelDatabaseHandler;

import java.io.PrintWriter;

/**
 * This class is a servlet that handles requests related to showing details and reviews for hotels.
 * It handles HTTP GET requests
 */
public class HotelsDetailsServlet extends HttpServlet {
    private final HotelDatabaseHandler hotelDatabaseHandler = HotelDatabaseHandler.getInstance();


    /**
     * Handles HTTP GET requests to display hotel details and reviews.
     * Retrieves the hotel by ID, reviews for the hotel, and calculates the average rating.
     * Uses Apache Velocity to render the hotel details page with the hotel information and reviews.
     *
     * @param request  The HTTP request object.
     * @param response The HTTP response object.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("velocityEngine");
            VelocityContext context = new VelocityContext();
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("currentUser");
            if (username != null && !username.isEmpty()) {
                context.put("username", username);
                String hotelId = StringEscapeUtils.escapeHtml4(request.getParameter("id"));
                if (hotelId != null && !hotelId.isEmpty()) {
                    Hotel hotel = hotelDatabaseHandler.getHotelById(hotelId);
                    context.put("hotel", hotel);
                    context.put("aveRating", String.format("%.2f", hotelDatabaseHandler.calculateAvgRating(hotelId)));
                }
            } else {
                context.put("username", null);
            }
            Template template = ve.getTemplate("static/hotelDetail.html");
            template.merge(context, out);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
