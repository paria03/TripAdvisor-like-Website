package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.ReviewsDatabaseHandler;

/**
 * This class is a servlet that handles requests related to deleting reviews for hotels.
 * It handles HTTP GET requests
 */
public class DeleteReviewServlet extends HttpServlet {
    private final ReviewsDatabaseHandler reviewsDatabaseHandler = ReviewsDatabaseHandler.getInstance();


    /**
     * Handles the HTTP GET request. This method processes the deletion of a review.
     *
     * @param request  The HttpServletRequest object containing the request data.
     * @param response The HttpServletResponse object used to send the response.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            String reviewId = request.getParameter("reviewId");
            String hotelId = request.getParameter("hotelId");
            reviewsDatabaseHandler.removeReview(reviewId);
            response.sendRedirect("hotelDetails?id=" + hotelId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
