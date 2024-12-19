package servlets;

import data.Review;
import hotelapp.ReviewData;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class is a servlet that handles requests related to adding reviews for hotels.
 * It handles both HTTP GET and POST requests
 */
public class AddReviewServlet extends HttpServlet {
    private final ReviewData reviewData;

    public AddReviewServlet(ReviewData reviewData) {
        super();
        this.reviewData = reviewData;
    }

    /**
     * Handles the HTTP GET request. This method renders the HTML page for adding a review.
     *
     * @param request  The HttpServletRequest object containing the request data.
     * @param response The HttpServletResponse object used to send the response.
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
            Template template = ve.getTemplate("static/addReview.html");
            context.put("username", username);

            template.merge(context, out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the HTTP POST request. This method processes the form data submitted by the user and adds a review.
     *
     * @param request  The HttpServletRequest object containing the form data.
     * @param response The HttpServletResponse object used to send the response.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("currentUser");
            String hotelId = request.getParameter("hotelId");
            String title = request.getParameter("title");
            int rating = Integer.parseInt(request.getParameter("rating"));
            String text = request.getParameter("text");

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedNow = now.format(formatter);

            Review review = new Review(hotelId, "", rating, title, text, username, formattedNow);
            reviewData.addReview(review, true);
            response.sendRedirect("/hotelDetails?id=" + hotelId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
