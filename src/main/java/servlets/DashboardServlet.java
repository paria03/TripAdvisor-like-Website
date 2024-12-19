package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.DatabaseHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet for managing the user's dashboard. It handles both GET and POST requests
 * related to displaying the dashboard and managing the visited links.
 */
public class DashboardServlet extends HttpServlet {

    /**
     * Handles the GET request to display the user's dashboard.
     * Retrieves the current user's visited links from the database
     * and merges the data with a Velocity template for rendering.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws IOException if an input or output error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("currentUser");
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

        List<String> links = databaseHandler.getAllVisitedLinks(username);
        PrintWriter out = response.getWriter();
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("velocityEngine");
        Template template = ve.getTemplate("static/dashboard.html");
        VelocityContext context = new VelocityContext();
        context.put("username", username);
        context.put("allLinks", links);
        template.merge(context, out);
    }

    /**
     * Handles the POST request to remove a link from the user's visited links.
     * It retrieves the current user's username and the link to remove,
     * then updates the database accordingly.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws IOException if an input or output error occurs
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("currentUser");
        String link = request.getParameter("link");

        if (username != null && link != null) {
            DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
            databaseHandler.removeVisitedLink(username, link);
        }
        response.sendRedirect("/dashboard");
    }

}
