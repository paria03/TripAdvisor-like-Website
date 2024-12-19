package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import server.DatabaseHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet for adding a new link to the user's visited links list.
 * It handles GET requests by retrieving the provided link and
 * adding it to the user's records in the database.
 */
public class ExpediaLinkServlet extends HttpServlet {

    /**
     * Handles the GET request to add a new link to the user's visited links.
     * It retrieves the link parameter from the request, identifies the current user,
     * and adds the link to the database.
     *
     * @param request  the HTTP request containing the link to be added
     * @param response the HTTP response used to return the result
     * @throws IOException if an input or output error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String link = request.getParameter("link");
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("currentUser");
        PrintWriter out = response.getWriter();

        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        out.println(databaseHandler.addLink(username, link));

    }
}
