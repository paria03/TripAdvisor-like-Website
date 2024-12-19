package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.UserDatabaseHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class is a servlet that handles requests related to registering new users.
 * It handles both HTTP GET and POST requests
 */
public class RegistrationServlet extends HttpServlet {
    private final UserDatabaseHandler userDatabaseHandler = UserDatabaseHandler.getInstance();

    /**
     * Handles HTTP GET requests to display the registration form.
     * If the user is not logged in, it returns the registration page.
     *
     * @param request  The HTTP request containing the user's data.
     * @param response The HTTP response used to send the registration form.
     * @throws IOException If an input or output error occurs while handling the request.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("velocityEngine");
        Template template = ve.getTemplate("static/registration.html");

        VelocityContext context = new VelocityContext();
        context.put("servletPath", request.getServletPath());
        context.put("errorMessage", "");
        template.merge(context, out);

    }

    /**
     * Handles HTTP POST requests to process the registration form.
     * It retrieves the user input (username and password), validates and registers the user using the DatabaseHandler,
     * and redirects the user to the home page if successful, or returns an error message if registration fails.
     *
     * @param request  The HTTP request containing the form data (username and password).
     * @param response The HTTP response used to return the result of registration.
     * @throws IOException If an input or output error occurs while handling the request.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        String username = StringEscapeUtils.escapeHtml4(request.getParameter("username"));
        String password = StringEscapeUtils.escapeHtml4(request.getParameter("password"));

        HttpSession session = request.getSession();

        if (userDatabaseHandler.registerUser(username, password)) {
            VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("velocityEngine");
            Template template = ve.getTemplate("static/homePage.html");

            VelocityContext context = new VelocityContext();
            String lastLogin = userDatabaseHandler.getLastLoginTime(username);
            if (lastLogin == null || lastLogin.isEmpty()) {
                context.put("lastLogin", "You have not logged in before");
            } else {
                context.put("lastLogin", lastLogin);
                session.setAttribute("lastLogin", lastLogin);
            }
            session.setAttribute("currentUser", username);
            context.put("username", username);

            LocalDateTime currentLoginTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String currentLoginFormatted = currentLoginTime.format(formatter);
            userDatabaseHandler.updateLastLoginTime(username, currentLoginFormatted);

            PrintWriter out = response.getWriter();
            template.merge(context, out);
        } else {
            VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("velocityEngine");
            Template template = ve.getTemplate("static/registration.html");
            VelocityContext context = new VelocityContext();
            context.put("errorMessage", "Registration Failed!!!");
            PrintWriter out = response.getWriter();
            template.merge(context, out);
        }
    }
}
