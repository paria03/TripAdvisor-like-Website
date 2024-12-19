package server;

import data.Review;

import java.sql.*;

/**
 * ReviewsDatabaseHandler handles interactions with the 'reviews' table in the database.
 * It provides methods to create the table, register new reviews, edit reviews, and perform other operations.
 */
public class ReviewsDatabaseHandler {
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    private final static ReviewsDatabaseHandler rwDbHandler = new ReviewsDatabaseHandler();

    /**
     * Returns the singleton instance of ReviewsDatabaseHandler
     *
     * @return instance of ReviewsDatabaseHandler
     */
    public static ReviewsDatabaseHandler getInstance() {
        return rwDbHandler;
    }

    /**
     * Creates the 'reviews' table in the database if it doesn't already exist.
     */
    public void createReviewsTable() {
        if (!databaseHandler.tableExists("reviews")) {
            Statement statement;
            try (Connection connection = databaseHandler.getConnection()) {
                statement = connection.createStatement();
                statement.executeUpdate(PreparedStatements.CREATE_REVIEWS_TABLE);
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Registers a new review in the database using the data from the Review object.
     * The method checks if the review already exists before inserting it.
     *
     * @param review The review object to be registered
     */
    public void registerDatesetReview(Review review) {
        PreparedStatement statement;
        if (!reviewExists(review, false)) {
            try (Connection connection = databaseHandler.getConnection()) {
                try {
                    statement = connection.prepareStatement(PreparedStatements.REGISTER_REVIEW_SQL);
                    statement.setString(1, review.getHotelId());
                    statement.setInt(2, review.getRating());
                    statement.setString(3, review.getTitle());
                    statement.setString(4, review.getText());
                    statement.setString(5, review.getNickName());
                    statement.setTimestamp(6, java.sql.Timestamp.valueOf(review.getDate() + " 00:00:00"));
                    statement.executeUpdate();

                } catch (Exception e) {
                    System.out.println(e);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Registers a new review in the database, checking if it already exists.
     * The date format is handled as a timestamp.
     *
     * @param review The review object to be registered
     */
    public void registerNewReview(Review review) {
        PreparedStatement statement;
        if (!reviewExists(review, true)) {
            try (Connection connection = databaseHandler.getConnection()) {
                try {
                    statement = connection.prepareStatement(PreparedStatements.REGISTER_REVIEW_SQL);
                    statement.setString(1, review.getHotelId());
                    statement.setInt(2, review.getRating());
                    statement.setString(3, review.getTitle());
                    statement.setString(4, review.getText());
                    statement.setString(5, review.getNickName());
                    statement.setTimestamp(6, java.sql.Timestamp.valueOf(review.getDate()));
                    statement.executeUpdate();

                } catch (Exception e) {
                    System.out.println(e);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Checks whether a specific review already exists in the database.
     *
     * @param review    The review object to check
     * @param newReview Flag indicating if this is a new review or not (affects how the date is handled)
     * @return True if the review exists, false otherwise
     */
    private boolean reviewExists(Review review, boolean newReview) {
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.REVIEW_EXISTS_SQL)) {

            statement.setString(1, review.getHotelId());
            statement.setInt(2, review.getRating());
            statement.setString(3, review.getTitle());
            statement.setString(4, review.getText());
            statement.setString(5, review.getNickName());
            if (newReview) {
                statement.setTimestamp(6, java.sql.Timestamp.valueOf(review.getDate()));
            } else {
                statement.setTimestamp(6, java.sql.Timestamp.valueOf(review.getDate() + " 00:00:00"));
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Edits an existing review in the database based on its reviewId.
     *
     * @param review The review object containing updated data
     */
    public void editeReview(Review review) {
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.EDITE_REVIEW_SQL)) {
            statement.setString(1, review.getTitle());
            statement.setString(2, review.getText());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(review.getDate()));
            statement.setString(4, review.getReviewId());
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Finds a review for a specific hotel hotelId and reviewId.
     *
     * @param hotelId  id of a hotel to find a specific review
     * @param reviewId id of the review to find
     * @return review information
     */
    public Review getReview(String hotelId, String reviewId) {
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_REVIEW_SQL)) {
            statement.setString(1, hotelId);
            statement.setString(2, reviewId);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                int rating = results.getInt("rating");
                String title = results.getString("title");
                String text = results.getString("text");
                String nickname = results.getString("nickname");
                String date = results.getString("date");
                return new Review(hotelId, reviewId, rating, title, text, nickname, date);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Removes a specific review for the given hotel id
     *
     * @param reviewId id of the review
     */
    public void removeReview(String reviewId) {
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.REMOVE_REVIEW_SQL)) {
            statement.setString(1, reviewId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error removing review: " + e.getMessage());
        }
    }
}
