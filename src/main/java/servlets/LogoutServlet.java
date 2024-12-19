package servlets;

import jakarta.servlet.http.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is a servlet that handles requests related to logout requests.
 * It handles HTTP GET requests
 */
public class LogoutServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests for logging out the user.
     * It invalidates the current session, effectively logging the user out,
     * and then redirects to the home page with the username cleared.
     *
     * @param request  The HTTP request containing the session information.
     * @param response The HTTP response used to send the rendered page.
     * @throws IOException If an input or output error occurs while handling the request.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("currentUser");
        PrintWriter out = response.getWriter();
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("velocityEngine");
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        if (username != null) {
            session.invalidate();
        }
        Template template = ve.getTemplate("static/homePage.html");
        VelocityContext context = new VelocityContext();
        context.put("username", "");
        template.merge(context, out);
    }
}