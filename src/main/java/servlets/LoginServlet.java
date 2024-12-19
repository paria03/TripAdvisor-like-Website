package servlets;

import jakarta.servlet.http.*;
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
 * This class is a servlet that handles requests related to login.
 * It handles both HTTP GET and POST requests
 */
public class LoginServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests.
     * If the user is not logged in, it displays the login form.
     * If the user is already logged in, it redirects them to the home page.
     *
     * @param request  The HTTP request containing the session information.
     * @param response The HTTP response used to send the rendered page.
     * @throws IOException If an input or output error occurs while handling the request.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("currentUser");

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("velocityEngine");
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        if (username == null) {
            Template template = ve.getTemplate("static/login.html");
            VelocityContext context = new VelocityContext();
            context.put("servletPath", request.getServletPath());
            context.put("errorMessage", "");
            template.merge(context, out);
        } else {
            // already logged in
            String lastLogin = (String) session.getAttribute("lastLogin");

            Template template = ve.getTemplate("static/homePage.html");
            VelocityContext context = new VelocityContext();
            context.put("username", username);
            context.put("lastLogin", lastLogin);
            template.merge(context, out);
        }
    }

    /**
     * Handles HTTP POST requests for user login.
     * It authenticates the user using the provided username and password.
     * If authentication is successful, the user is redirected to the home page.
     * If authentication fails, an error message is displayed on the login page.
     *
     * @param request  The HTTP request containing the login credentials.
     * @param response The HTTP response used to send the rendered page.
     * @throws IOException If an input or output error occurs while handling the request.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = StringEscapeUtils.escapeHtml4(request.getParameter("username"));
        String password = StringEscapeUtils.escapeHtml4(request.getParameter("password"));
        UserDatabaseHandler userDatabaseHandler = UserDatabaseHandler.getInstance();
        boolean authenticated = userDatabaseHandler.authenticateUser(username, password);
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("velocityEngine");

        if (authenticated) {
            //after login
            Template template = ve.getTemplate("static/homePage.html");
            VelocityContext context = new VelocityContext();
            HttpSession session = request.getSession();

            session.setAttribute("currentUser", username);
            String lastLogin = userDatabaseHandler.getLastLoginTime(username);

            if (lastLogin == null || lastLogin.isEmpty()) {
                context.put("lastLogin", "You have not logged in before");

            } else {
                context.put("lastLogin", lastLogin);
                session.setAttribute("lastLogin", lastLogin);
            }
            //saving the current login time for the next time they log in

            LocalDateTime currentLoginTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatted = currentLoginTime.format(formatter);
            userDatabaseHandler.updateLastLoginTime(username, formatted);
            //new login

            context.put("username", username);
            PrintWriter out = response.getWriter();
            template.merge(context, out);
        } else {
            PrintWriter out = response.getWriter();
            Template template = ve.getTemplate("static/login.html");
            VelocityContext context = new VelocityContext();
            context.put("errorMessage", "Username or Password is not correct");
            template.merge(context, out);
        }

    }
}