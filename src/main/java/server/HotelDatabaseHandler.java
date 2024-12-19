package server;

import data.Hotel;
import data.Review;
import hotelapp.InvalidRatingException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles connection and operations on database for hotels operations
 */
public class HotelDatabaseHandler {
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    private final static HotelDatabaseHandler hotelDatabaseHandler = new HotelDatabaseHandler();

    /**
     * Returns the singleton instance of HotelDatabaseHandler.
     *
     * @return the singleton instance of HotelDatabaseHandler
     */
    public static HotelDatabaseHandler getInstance() {
        return hotelDatabaseHandler;
    }

    /**
     * Creates the hotels table in the database if it doesn't exist.
     */
    public void createHotelsTable() {
        if (!databaseHandler.tableExists("hotels")) {
            Statement statement;
            try (Connection connection = databaseHandler.getConnection()) {
                statement = connection.createStatement();
                statement.executeUpdate(PreparedStatements.CREATE_HOTELS_TABLE);
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Checks if a hotel exists in the database by its hotelId.
     *
     * @param hotelId the hotelId to check
     * @return true if the hotel exists, false otherwise
     */
    private boolean hotelExists(String hotelId) {
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.HOTEL_EXISTS_SQL)) {

            statement.setString(1, hotelId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking hotel existence: " + e.getMessage());
        }
        return false;
    }

    /**
     * Registers a new hotel in the database.
     *
     * @param hotel the hotel to register
     */
    public void registerHotel(Hotel hotel) {
        PreparedStatement statement;
        if (!hotelExists(hotel.getId())) {
            try (Connection connection = databaseHandler.getConnection()) {
                try {
                    statement = connection.prepareStatement(PreparedStatements.REGISTER_HOTEL_SQL);
                    statement.setString(1, hotel.getId());
                    statement.setString(2, hotel.getName());
                    statement.setString(3, hotel.getAddress());
                    statement.setString(4, hotel.getCity());
                    statement.setString(5, hotel.getState());
                    statement.setString(6, hotel.getLat());
                    statement.setString(7, hotel.getLng());
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
     * Calculates the average rating for a hotel based on its hotelId.
     *
     * @param hotelId the hotelId of the hotel
     * @return the average rating for the hotel
     */
    public double calculateAvgRating(String hotelId) {
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.AVG_RATING_SQL)) {
            statement.setString(1, hotelId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("average_rating");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0.0;
    }

    /**
     * Retrieves a hotel by its hotelId.
     *
     * @param hotelId the hotelId of the hotel to retrieve
     * @return the hotel with the given hotelId, or null if not found
     */
    public Hotel getHotelById(String hotelId) {
        try (Connection connection = databaseHandler.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.HOTEL_BY_ID_SQL)) {
                statement.setString(1, hotelId);
                ResultSet results = statement.executeQuery();
                if (results.next()) {
                    String name = results.getString("name");
                    String address = results.getString("address");
                    String city = results.getString("city");
                    String state = results.getString("state");
                    String lat = results.getString("lat");
                    String lng = results.getString("lng");

                    return new Hotel(name, hotelId, address, city, state, lat, lng);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a hotel by its name.
     *
     * @param name the name of the hotel to retrieve
     * @return the hotel with the given name, or null if not found
     */
    public Hotel getHotelByName(String name) {
        try (Connection connection = databaseHandler.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.HOTEL_BY_NAME_SQL)) {
                statement.setString(1, name);
                ResultSet results = statement.executeQuery();
                if (results.next()) {
                    String hotelId = results.getString("hotelId");
                    String address = results.getString("address");
                    String city = results.getString("city");
                    String state = results.getString("state");
                    String lat = results.getString("lat");
                    String lng = results.getString("lng");
                    return new Hotel(name, hotelId, address, city, state, lat, lng);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Retrieves all reviews for a hotel with pagination.
     *
     * @param hotelId the hotelId of the hotel to retrieve reviews for
     * @param limit   the maximum number of reviews to retrieve
     * @param offset  the offset to start retrieving reviews from
     * @return a list of reviews for the hotel
     */
    public List<Review> getAllHotelReviews(String hotelId, int limit, int offset) {
        List<Review> reviews = new ArrayList<>();
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.ALL_REVIEWS_SQL)) {
            statement.setString(1, hotelId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);

            ResultSet results = statement.executeQuery();
            while (results.next()) {
                int reviewId = results.getInt("reviewId");
                int rating = results.getInt("rating");
                String title = results.getString("title");
                String text = results.getString("text");
                String nickname = results.getString("nickname");
                String date = results.getString("date");
                reviews.add(new Review(hotelId, String.valueOf(reviewId), rating, title, text, nickname, date));
            }
        } catch (SQLException | InvalidRatingException e) {
            throw new RuntimeException("Error retrieving all hotels", e);
        }
        return reviews;
    }

    /**
     * Retrieves the names of all hotels in the database.
     *
     * @return a list of hotel names
     */
    public List<String> getAllHotelsName() {
        List<String> hotelsName = new ArrayList<>();
        try (Connection connection = databaseHandler.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.ALL_HOTELS_NAME_SQL)) {
                ResultSet results = statement.executeQuery();
                while (results.next()) {
                    String name = results.getString("name");
                    hotelsName.add(name);
                }
                return hotelsName;
            } catch (SQLException e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    /**
     * Retrieves all hotels from the database.
     *
     * @return a list of all hotels
     */
    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = new ArrayList<>();
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.ALL_HOTELS_SQL);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                String id = results.getString("hotelId");
                String name = results.getString("name");
                String address = results.getString("address");
                String city = results.getString("city");
                String state = results.getString("state");
                String lat = results.getString("lat");
                String lng = results.getString("lng");
                hotels.add(new Hotel(name, id, address, city, state, lat, lng));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all hotels", e);
        }
        return hotels;
    }

}
