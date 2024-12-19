package server;

import hotelapp.HotelLoader;
import hotelapp.ReviewData;
import hotelapp.ReviewDirectoryTraverser;
import hotelapp.ThreadSafeReviewData;
import parser.ArgumentParser;
import parser.ReviewParser;

public class TravelServerDriver {
    public static final int PORT = 8080;
    private final static HotelLoader hotelLoader = new HotelLoader();
    private final static ReviewData threadSafeReviewData = new ThreadSafeReviewData();
    private final ArgumentParser argumentParser = new ArgumentParser();
    private final DatabaseHandler databaseHandler=DatabaseHandler.getInstance();
    private final UserDatabaseHandler userDatabaseHandler=UserDatabaseHandler.getInstance();
    private final ReviewsDatabaseHandler rwDbHandler= ReviewsDatabaseHandler.getInstance();
    private final HotelDatabaseHandler hotelDatabaseHandler= HotelDatabaseHandler.getInstance();

    /**
     * Parse given arguments that contain paths to the hotel file and the reviews folder,
     * and load hotel and review data into the corresponding data structures.
     *
     * @param args Arguments can be given in the following format where -reviews, -threads, -output are optional:
     *             -hotels pathToHotelFile -reviews pathToReviewsFolder -threads n
     *             or in a different order.
     */
    public void loadData(String[] args) {
        argumentParser.parseArgs(args);
        String hotelPath = argumentParser.getArgValue("-hotels");
        if (hotelPath == null || hotelPath.isEmpty()) {
            throw new IllegalArgumentException("The essential parameter \"-hotels\" is missing");
        }
        if (!databaseHandler.tableExists("hotels")) {
            hotelDatabaseHandler.createHotelsTable();
            hotelLoader.updateHotelMap(hotelPath);
        }else if(!databaseHandler.tableExists("expediaLinks")){
            databaseHandler.createExpediaLinksTable();
        }
        String reviewPath = argumentParser.getArgValue("-reviews");
        String threads = argumentParser.getArgValue("-threads");
        int threadNum;
        if (threads == null) {
            threadNum = 3;
        } else {
            threadNum = Integer.parseInt(threads);
        }
        ReviewParser reviewParser;
        ReviewDirectoryTraverser reviewDirectoryTraverser;
        if (reviewPath != null && !databaseHandler.tableExists("reviews") || !databaseHandler.tableExists("users")) {
            rwDbHandler.createReviewsTable();
            userDatabaseHandler.createUserTable();
            reviewParser = new ReviewParser();
            reviewDirectoryTraverser = new ReviewDirectoryTraverser(threadNum, reviewParser);
            reviewDirectoryTraverser.traverseDirectory(reviewPath, (ThreadSafeReviewData) threadSafeReviewData);

        }
    }

    public static void main(String[] args) {
        TravelServerDriver server = new TravelServerDriver();
        try {
            server.loadData(args);
            JettyServer jettyServer = new JettyServer(PORT, threadSafeReviewData, hotelLoader);
            jettyServer.addServletMapping();
            jettyServer.start();

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
