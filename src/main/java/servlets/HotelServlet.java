package servlets;

import data.Hotel;
import hotelapp.HotelLoader;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.HotelDatabaseHandler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a servlet that handles requests related to searching for hotels.
 * It handles HTTP GET requests
 */
public class HotelServlet extends HttpServlet {
    private final HotelLoader hotelLoader;
    private final HotelDatabaseHandler hotelDatabaseHandler = HotelDatabaseHandler.getInstance();

    public HotelServlet(HotelLoader hotelLoader) {
        super();
        this.hotelLoader = hotelLoader;
    }

    /**
     * Handles HTTP GET requests for fetching hotel information based on the provided hotelId or hotelName.
     * If no parameters are provided, it returns all hotels.
     * The results are rendered using the Velocity template engine.
     *
     * @param request  The HTTP request containing the hotelId or hotelName parameters.
     * @param response The HTTP response used to send the rendered page.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();

            VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("velocityEngine");
            VelocityContext context = new VelocityContext();
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("currentUser");

            if (username == null) {
                context.put("username", "");
            } else {
                context.put("username", username);
            }
            String lastLogin = (String) session.getAttribute("lastLogin");
            if (lastLogin == null || lastLogin.isEmpty()) {
                context.put("lastLogin", "You have not logged in before");

            } else {
                context.put("lastLogin", lastLogin);
            }

            String hotelId = StringEscapeUtils.escapeHtml4(request.getParameter("hotelId"));
            String hotelName = request.getParameter("hotelName");

            List<Hotel> hotels = new ArrayList<>();

            if (hotelId != null && !hotelId.isEmpty()) {//show hotels with id
                Hotel hotel = hotelDatabaseHandler.getHotelById(hotelId);
                if (hotel != null) {
                    hotels.add(hotel);
                    context.put("hotels", hotels);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else if (hotelName != null && !hotelName.isEmpty()) {//hotel name
                context.put("hotels", hotelLoader.findHotelsByKeyword(hotelName));
            } else {//show all
                context.put("hotels", hotelDatabaseHandler.getAllHotels());
            }
            Template template = ve.getTemplate("static/homePage.html");
            template.merge(context, out);
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }
}
