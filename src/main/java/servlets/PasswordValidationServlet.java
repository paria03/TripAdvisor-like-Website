package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import server.UserDatabaseHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is a servlet that handles requests related to validating entered passwords.
 * It handles HTTP GET requests
 */
public class PasswordValidationServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests for validating the password strength.
     * It checks if the provided password contains the required criteria:
     * - Lowercase and uppercase letters
     * - Special characters ('@', '#', '$', '%')
     * - Numbers
     * - At least 8 characters in length
     * <p>
     * If the password does not meet the requirements, a message with the
     * password criteria is returned to the user.
     *
     * @param request  The HTTP request containing the password parameter to be validated.
     * @param response The HTTP response used to return the validation result.
     * @throws IOException If an input or output error occurs while handling the request.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        String password = StringEscapeUtils.escapeHtml4(request.getParameter("password"));
        UserDatabaseHandler userDatabaseHandler = UserDatabaseHandler.getInstance();
        if (!userDatabaseHandler.isPassValid(password)) {
            out.println("Password should contain:\r\n" +
                    " Lowercase Letters \r\n" +
                    "Uppercase Letters \r\n" +
                    "Special characters ('@' '#' '$' '%') \r\n" +
                    "Numbers \r\n" +
                    "And should be at least 8 characters long\r"
            );

        }
    }
}
