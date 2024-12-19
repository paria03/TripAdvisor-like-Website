package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import server.UserDatabaseHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is a servlet that handles requests related validating entered username by the user.
 * It handles HTTP GET requests
 */
public class UsernameValidationServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests to validate the availability of a username.
     * The servlet checks if the given username already exists in the database.
     * If the username is taken, a message indicating so is returned as plain text.
     *
     * @param request  The HTTP request containing the username to be checked.
     * @param response The HTTP response where the validation result will be sent.
     * @throws IOException If an input or output error occurs while handling the request.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        String username = StringEscapeUtils.escapeHtml4(request.getParameter("username"));
        UserDatabaseHandler userDatabaseHandler = UserDatabaseHandler.getInstance();
        if (userDatabaseHandler.userExists(username)) {
            out.println("Sorry, this username \"" + username + "\" already exists.");
        }
    }
}
