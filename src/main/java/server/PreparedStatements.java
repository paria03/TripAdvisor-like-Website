package server;

public class PreparedStatements {
    /**
     * Prepared Statements
     */

    // SQL query for creating the users table in the database
    public static final String CREATE_USER_TABLE =
            "CREATE TABLE if not exists users (" +
                    "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL, " +
                    "lastLogin TIMESTAMP );";

    // SQL query for creating the reviews table in the database
    public static final String CREATE_REVIEWS_TABLE =
            "CREATE TABLE if not exists reviews (" +
                    "reviewId INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "hotelId VARCHAR(10) NOT NULL , " +
                    "rating INTEGER NOT NULL, " +
                    "title VARCHAR(100) , " +
                    "text TEXT NOT NULL, " +
                    "nickname VARCHAR(50) NOT NULL, " +
                    "date DATETIME NOT NULL);";

    // SQL query for creating the reviewsLikes table, which stores likes for reviews
    public static String CREATE_REVIEWS_LIKES_TABLE =
            " CREATE TABLE if not exists reviewsLikes (" +
                    " likeId INT AUTO_INCREMENT PRIMARY KEY," +
                    " reviewId INTEGER, " +
                    " username VARCHAR(50) NOT NULL);";

    // SQL query for creating the hotels table to store hotel data
    public static final String CREATE_HOTELS_TABLE =
            "CREATE TABLE if not exists hotels (" +
                    "hotelId INTEGER PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL , " +
                    "address VARCHAR(100) NOT NULL, " +
                    "city VARCHAR(100) NOT NULL, " +
                    "state VARCHAR(100) NOT NULL, " +
                    "lat VARCHAR(20) NOT NULL, " +
                    "lng VARCHAR(100) NOT NULL);";

    // SQL query for creating the expediaLinks table to store links for users
    public static final String CREATE_TABLE_EXPEDIA_LINKS =
            "CREATE TABLE expediaLinks (" +
                    " id INT AUTO_INCREMENT PRIMARY KEY," +
                    " username VARCHAR(50) NOT NULL," +
                    " link TEXT NOT NULL);";


    // SQL query to insert a new user into the database
    public static final String REGISTER_USER_SQL =
            "INSERT INTO users (username, password, usersalt, lastLogin) " +
                    "VALUES (?, ?, ?,?);";

    // SQL query to register a new hotel in the database
    public static final String REGISTER_HOTEL_SQL =
            "INSERT INTO hotels (hotelId, name, address, city, state,lat,lng) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";

    // SQL query to insert a new review into the database
    public static final String REGISTER_REVIEW_SQL =
            "INSERT INTO reviews (hotelId, rating, title, text, nickname,date) " +
                    " VALUES (?, ?, ?, ?, ?, ?);";

    // SQL query to add a like for a review in the reviewsLikes table
    public static final String ADD_LIKE_SQL =
            "INSERT INTO  reviewsLikes(username, reviewId) " +
                    " VALUES (?, ?);";

    // SQL query to insert a new link in the expediaLinks table
    public static final String ADD_LINK_SQL =
            "INSERT INTO  expediaLinks(username, link) " +
                    " VALUES (?, ?);";

    // SQL query to retrieve all reviews for a specific hotel
    public static final String ALL_REVIEWS_SQL =
            "SELECT * FROM reviews" +
                    " WHERE hotelId=?" +
                    " ORDER BY date DESC" +
                    " LIMIT ? OFFSET ?;";

    // SQL query to retrieve all links for a specific user
    public static final String ALL_LINKS_SQL =
            "SELECT * FROM expediaLinks" +
                    " WHERE username=?;";

    // SQL query to retrieve the last login timestamp for a specific user
    public static String GET_LAST_LOGIN_SQL =
            "SELECT lastLogin FROM users WHERE username = ?;";

    // SQL query to update the last login timestamp for a specific user
    public static String UPDATE_LAST_LOGIN_SQL =
            "UPDATE users SET lastLogin = ? WHERE username = ?;";

    // SQL query to get the number of likes for a specific review
    public static final String NUMBER_OF_LIKES_SQL =
            "SELECT COUNT(*) as likesCount" +
                    " FROM reviewsLikes WHERE reviewId = ?";

    // SQL query to retrieve a specific review for a hotel by hotelId and reviewId
    public static final String GET_REVIEW_SQL =
            "SELECT * FROM reviews" +
                    " WHERE hotelId=? AND reviewId=? ";

    // SQL query to check if a specific user has liked a specific review
    public static final String GET_LIKED_REVIEW_SQL =
            "SELECT COUNT(*) FROM reviewsLikes" +
                    " WHERE username=? AND reviewId=? ";

    // SQL query to check if a user has visited a specific link
    public static final String GET_VISITED_LINKS_SQL =
            "SELECT COUNT(*) FROM expediaLinks" +
                    " WHERE username=? AND link=? ";

    // SQL query to get the average rating of a specific hotel
    public static final String AVG_RATING_SQL =
            "SELECT AVG(rating) AS average_rating " +
                    "FROM reviews " +
                    "WHERE hotelId = ?;";

    // SQL query to check if a hotel exists by hotelId
    public static final String HOTEL_EXISTS_SQL =
            "SELECT COUNT(*) FROM hotels WHERE hotelId = ?;";

    // SQL query to check if a review exists with a specific set of parameters
    public static final String REVIEW_EXISTS_SQL =
            "SELECT COUNT(*) FROM reviews WHERE hotelId = ? AND rating = ?  AND title = ? AND text = ?  AND nickname = ? AND date = ?; ";

    // SQL query to remove a review by reviewId
    public static final String REMOVE_REVIEW_SQL =
            "DELETE FROM reviews WHERE reviewId = ?;";

    // SQL query to remove a visited link for a user by username and link
    public static final String REMOVE_VISITED_LINK_SQL =
            "DELETE FROM expediaLinks WHERE username = ? AND link = ?;";

    // SQL query to edit an existing review
    public static final String EDITE_REVIEW_SQL =
            "UPDATE reviews " +
                    "SET title = ?, text = ?, date = ? " +
                    "WHERE reviewId = ?;";

    //SQL query to retrieve the salt for a specific user
    public static final String SALT_SQL =
            "SELECT usersalt FROM users WHERE username = ?;";

    // SQL query to retrieve hotel details by hotelId
    public static final String HOTEL_BY_ID_SQL =
            "SELECT name, address, city, state, lat, lng" +
                    " FROM hotels where hotelId = ?;";

    // SQL query to retrieve all hotel names
    public static final String ALL_HOTELS_NAME_SQL =
            "SELECT name From hotels ;";

    // SQL query to retrieve all hotel details
    public static final String ALL_HOTELS_SQL =
            "SELECT hotelId, name, address, city, state, lat, lng FROM hotels";

    // SQL query to retrieve hotel details by hotel name
    public static final String HOTEL_BY_NAME_SQL = "SELECT * FROM hotels WHERE name = ?";

    // SQL query to authenticate a user by username and password
    public static final String AUTH_SQL =
            "SELECT username FROM users " +
                    "WHERE username = ? AND password = ?;";


    // SQL query to check if a username already exists in the database
    public static final String USER_EXISTS_SQL =
            "SELECT COUNT(*) FROM users " +
                    "WHERE username=? ;";

}