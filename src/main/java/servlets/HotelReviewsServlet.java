package servlets;

import com.google.gson.Gson;
import data.Review;
import hotelapp.ReviewData;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet for handling hotel review requests.
 * It retrieves hotel reviews based on pagination (offset and limit)
 * and sends them back as a JSON response.
 */
public class HotelReviewsServlet extends HttpServlet {
    private final ReviewData reviewData;

    public HotelReviewsServlet(ReviewData reviewData) {
        super();
        this.reviewData = reviewData;
    }

    /**
     * Handles GET requests to retrieve hotel reviews.
     * Supports pagination with actions to move forwards or backwards in the list of reviews.
     * The reviews are returned as a JSON array.
     *
     * @param request  the HTTP request containing parameters like hotelId and action (next/back)
     * @param response the HTTP response where reviews are sent back as JSON
     * @throws IOException if an input or output error occurs during response handling
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        int offset = 0;
        int limit = 5;
        HttpSession session = request.getSession();
        Object res = session.getAttribute("offset");
        if (res == null) {
            offset = 0;
        } else {
            offset = (int) res;
        }
        String action = request.getParameter("action");
        if (action.equals("next")) {
            offset = offset + limit;

        } else if (action.equals("back")) {
            offset = Math.max(0, offset - limit);

        }
        session.setAttribute("offset", offset);

        String hotelId = StringEscapeUtils.escapeHtml4(request.getParameter("hotelId"));
        if (hotelId != null && !hotelId.isEmpty()) {
            List<Review> reviews = reviewData.getReviews(hotelId, limit, offset);
            if (reviews != null && !reviews.isEmpty()) {
                Gson gson = new Gson();
                String jsonReviews = gson.toJson(reviews);
                out.println(jsonReviews);
            } else {
                out.println("{\"message\": \"No more reviews available.\", \"noMoreReviews\": true}");
            }
        }
    }

}
