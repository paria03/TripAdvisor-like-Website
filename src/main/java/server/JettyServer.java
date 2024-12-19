package server;


import hotelapp.HotelLoader;
import hotelapp.ReviewData;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.*;

/**
 * This class uses Jetty & servlets to implement a web server responding to Http GET requests
 */
public class JettyServer {
    private final Server jettyServer;
    private final ServletContextHandler handler;
    private final ReviewData threadSafeReviewData;
    private final HotelLoader hotelLoader;

    public JettyServer(int port, ReviewData threadSafeReviewData, HotelLoader hotelLoader) {
        jettyServer = new Server(port);
        handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        this.threadSafeReviewData = threadSafeReviewData;
        this.hotelLoader = hotelLoader;
    }

    /**
     * Maps servlets to different URL paths
     */
    public void addServletMapping() {

        handler.addServlet(new ServletHolder(new RegistrationServlet()), "/registration");
        handler.addServlet(new ServletHolder(new LoginServlet()), "/login");
        handler.addServlet(new ServletHolder(new LogoutServlet()), "/logout");
        handler.addServlet(new ServletHolder(new HomePageServlet()), "/homePage");
        handler.addServlet(new ServletHolder(new UsernameValidationServlet()), "/validateUsername");
        handler.addServlet(new ServletHolder(new PasswordValidationServlet()), "/validatePassword");
        handler.addServlet(new ServletHolder(new HotelServlet(hotelLoader)), "/search");
        handler.addServlet(new ServletHolder(new HotelsDetailsServlet()), "/hotelDetails");
        handler.addServlet(new ServletHolder(new AddReviewServlet(threadSafeReviewData)), "/addReview");
        handler.addServlet(new ServletHolder(new EditReviewServlet()), "/editReview");
        handler.addServlet(new ServletHolder(new DeleteReviewServlet()), "/deleteReview");
        handler.addServlet(new ServletHolder(new HotelReviewsServlet(threadSafeReviewData)), "/hotelReviews");
        handler.addServlet(new ServletHolder(new LikeReviewServlet()), "/likeReview");
        handler.addServlet(new ServletHolder(new ExpediaLinkServlet()), "/expediaLink");
        handler.addServlet(new ServletHolder(new DashboardServlet()), "/dashboard");

    }

    /**
     * Sets up the server, including the Velocity engine for template rendering and configuring the
     * resource handler to serve static files.
     */
    private void setUp() {
        VelocityEngine velocity = new VelocityEngine();
        velocity.init();
        handler.setAttribute("velocityEngine", velocity);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("static");
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, handler});

        jettyServer.setHandler(handlers);
    }

    /**
     * Function that starts the server
     *
     * @throws Exception throws exception if access failed
     */
    public void start() throws Exception {
        setUp();
        jettyServer.start();
        jettyServer.join();
    }
}