package servlets;

import data.Review;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.ReviewsDatabaseHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is a servlet that handles requests related to editing reviews for hotels.
 * It handles both HTTP GET and POST requests
 */
public class EditReviewServlet extends HttpServlet {
    private Review review;
    private String hotelId;
    private final ReviewsDatabaseHandler reviewsDatabaseHandler = ReviewsDatabaseHandler.getInstance();


    /**
     * Handles the HTTP GET request to retrieve the review data and display the edit form.
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

            String reviewId = request.getParameter("reviewId");
            hotelId = request.getParameter("hotelId");
            review = reviewsDatabaseHandler.getReview(hotelId, reviewId);

            VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("velocityEngine");
            VelocityContext context = new VelocityContext();
            context.put("review", review);
            Template template = ve.getTemplate("static/editReview.html");
            template.merge(context, out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the HTTP POST request to update the review text and title.
     *
     * @param request  The HttpServletRequest object containing the updated review data.
     * @param response The HttpServletResponse object used to send the response.
     * @throws IOException If an IO error occurs during the response.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String text = request.getParameter("text");
        review.setText(text);
        review.setTitle(title);
        reviewsDatabaseHandler.editeReview(review);
        response.sendRedirect("/hotelDetails?id=" + hotelId);
    }
}
