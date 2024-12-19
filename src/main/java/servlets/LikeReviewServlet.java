package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import server.DatabaseHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet that handles the process of liking a review.
 * It allows users to like a review and updates the like count.
 * Returns a JSON response with the updated like count or a message indicating if the user has already liked the review.
 */
public class LikeReviewServlet extends HttpServlet {

    /**
     * Handles POST requests to like a review.
     * This method retrieves the review ID and current user from the session,
     * adds a like to the review if the user has not liked it before, and returns the updated like count as a JSON response.
     *
     * @param request  the HTTP request containing the review ID and user data
     * @param response the HTTP response where the updated like count or message is sent back in JSON format
     * @throws IOException if an input or output error occurs during response handling
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int reviewId = Integer.parseInt(request.getParameter("reviewId"));
        HttpSession session = request.getSession();
        String currentUser = (String) session.getAttribute("currentUser");

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.createLikesTable();
        int likeCount = dbHandler.getLikeCount(reviewId);

        if (dbHandler.AddLike(currentUser, reviewId)) {
            likeCount += 1;
            out.print("{\"likeCount\": " + likeCount + "}");
        } else {
            out.print("{\"message\":\"You have liked this review before\"}");
        }
    }

}
