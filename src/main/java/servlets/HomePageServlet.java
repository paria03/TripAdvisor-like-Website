package servlets;

import jakarta.servlet.http.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class is a servlet that handles requests related to searching for hotels and the main page.
 * It handles HTTP GET requests
 */
public class HomePageServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests for the home page. It retrieves the username
     * from the session, sets up the context for the Velocity template,
     * and renders the home page HTML.
     *
     * @param request  The HTTP request object.
     * @param response The HTTP response object.
     * @throws IOException If an IO error occurs while writing the response.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("currentUser");

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("velocityEngine");
        VelocityContext context = new VelocityContext();
        if (username != null) {
            context.put("username", username);
        } else {
            context.put("username", "");
        }
        context.put("hotels", new ArrayList<>());
        context.put("allReviews", new HashSet<>());
        context.put("lastLogin", session.getAttribute("lastLogin"));

        Template template = ve.getTemplate("static/homePage.html");
        template.merge(context, out);
    }
}