package server;


import java.io.FileReader;
import java.io.IOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class handles connection and operations on database
 */
public class DatabaseHandler {

    private static final DatabaseHandler dbHandler = new DatabaseHandler("database.properties");
    private final Properties config;
    private String uri = null;

    private DatabaseHandler(String propertiesFile) {
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://" + config.getProperty("hostname") + "/" + config.getProperty("database") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false";
    }

    /**
     * Returns the instance of the database handler.
     *
     * @return instance of the database handler
     */
    public static DatabaseHandler getInstance() {
        return dbHandler;
    }

    /**
     * Loads configuration properties from the specified file.
     *
     * @param propertyFile propertyFile The path to the properties file to be loaded.
     * @return A Properties object containing the loaded configuration properties.
     */
    private Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)) {
            config.load(fr);
        } catch (IOException e) {
            System.out.println(e);
        }

        return config;
    }

    /**
     * Creates the "reviewsLikes" table if it doesn't exist.
     */
    public void createLikesTable() {
        if (!tableExists("reviewsLikes")) {
            Statement statement;
            try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
                statement = dbConnection.createStatement();
                statement.executeUpdate(PreparedStatements.CREATE_REVIEWS_LIKES_TABLE);
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Creates the "expediaLinks" table if it doesn't exist.
     */
    public void createExpediaLinksTable() {
        if (!tableExists("expediaLinks")) {
            Statement statement;
            try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
                statement = dbConnection.createStatement();
                statement.executeUpdate(PreparedStatements.CREATE_TABLE_EXPEDIA_LINKS);
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Checks if the given table exists in the database.
     *
     * @param tableName The name of the table to check.
     * @return True if the table exists, false otherwise.
     */
    public boolean tableExists(String tableName) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, config.getProperty("database"), tableName, new String[]{"TABLE"});
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error checking table existence: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves a new database connection.
     *
     * @return A Connection object representing the database connection.
     * @throws SQLException If an SQL error occurs during connection.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
    }

    /**
     * Adds a like for a given review by a user.
     *
     * @param username The username of the user liking the review.
     * @param reviewId The ID of the review being liked.
     * @return True if the like was successfully added, false if the user already liked the review.
     */
    public boolean AddLike(String username, int reviewId) {
        if (!liked(username, reviewId)) {
            try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
                 PreparedStatement statement = connection.prepareStatement(PreparedStatements.ADD_LIKE_SQL)) {
                statement.setString(1, username);
                statement.setInt(2, reviewId);
                statement.executeUpdate();
            } catch (Exception e) {
                System.out.println(e);
            }
            return true;

        } else {
            return false;
        }
    }

    /**
     * Adds a visited link for a user.
     *
     * @param username The username of the user.
     * @param link     The link the user visited.
     * @return True if the link was successfully added, false if the user already visited the link.
     */
    public boolean addLink(String username, String link) {
        if (!visited(username, link)) {
            try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
                 PreparedStatement statement = connection.prepareStatement(PreparedStatements.ADD_LINK_SQL)) {
                statement.setString(1, username);
                statement.setString(2, link);
                statement.executeUpdate();
            } catch (Exception e) {
                System.out.println(e);
            }
            return true;

        } else {
            return false;
        }
    }

    /**
     * Gets the like count for a specific review.
     *
     * @param reviewId The ID of the review.
     * @return The number of likes for the given review.
     */
    public int getLikeCount(int reviewId) {
        if (tableExists("reviewsLikes")) {
            try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
                 PreparedStatement statement = connection.prepareStatement(PreparedStatements.NUMBER_OF_LIKES_SQL)) {
                statement.setInt(1, reviewId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("likesCount");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return 0;
    }

    /**
     * Retrieves all visited links for a specific user.
     *
     * @param username The username of the user.
     * @return A list of visited links by the user.
     */
    public List<String> getAllVisitedLinks(String username) {
        List<String> links = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.ALL_LINKS_SQL)) {
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                links.add(results.getString("link"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all links");
        }
        return links;
    }

    /**
     * Checks if a specific user has liked a given review.
     *
     * @param username The username of the user.
     * @param reviewId The ID of the review.
     * @return True if the user has liked the review, false otherwise.
     */
    public boolean liked(String username, int reviewId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_LIKED_REVIEW_SQL)) {
            statement.setString(1, username);
            statement.setInt(2, reviewId);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return results.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Checks if a user has visited a specific link.
     *
     * @param username The username of the user.
     * @param link     The link to check.
     * @return True if the user has visited the link, false otherwise.
     */
    public boolean visited(String username, String link) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_VISITED_LINKS_SQL)) {
            statement.setString(1, username);
            statement.setString(2, link);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return results.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Removes a visited link from the user's record.
     *
     * @param username The username of the user.
     * @param link     The link to be removed.
     */
    public void removeVisitedLink(String username, String link) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.REMOVE_VISITED_LINK_SQL)) {
            statement.setString(1, username);
            statement.setString(2, link);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error removing review: " + e.getMessage());
        }
    }
}