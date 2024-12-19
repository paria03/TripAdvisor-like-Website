package server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * This class handles all user-related database operations such as
 * creating user tables, registering users, authenticating users,
 * and managing user login times. It handles password hashing and
 * user validation.
 */
public class UserDatabaseHandler {
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    private static final UserDatabaseHandler userDatabaseHandler = new UserDatabaseHandler();
    private final Random random = new Random();

    /**
     * Returns the singleton instance of the UserDatabaseHandler.
     *
     * @return the instance of UserDatabaseHandler
     */

    public static UserDatabaseHandler getInstance() {
        return userDatabaseHandler;
    }

    /**
     * Creates the user table in the database if it doesn't already exist.
     * The table stores information related to the users such as username,
     * hashed password, salt, and the last login time.
     */
    public void createUserTable() {
        if (!databaseHandler.tableExists("users")) {
            Statement statement;
            try (Connection connection = databaseHandler.getConnection()) {
                statement = connection.createStatement();
                statement.executeUpdate(PreparedStatements.CREATE_USER_TABLE);
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }


    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database.
     *
     * @param newUser - username of new user
     * @param newPass - password of new user
     */
    public boolean registerUser(String newUser, String newPass) {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        if (!userExists(newUser)) {
            if (isPassValid(newPass)) {
                String userSalt = encodeHex(saltBytes, 32);
                String passHash = getHash(newPass, userSalt);

                PreparedStatement statement;
                try (Connection connection = databaseHandler.getConnection()) {
                    try {
                        statement = connection.prepareStatement(PreparedStatements.REGISTER_USER_SQL);
                        statement.setString(1, newUser);
                        statement.setString(2, passHash);
                        statement.setString(3, userSalt);
                        statement.setString(4, null);
                        statement.executeUpdate();
                        statement.close();
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex);
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * Connects to the database and checks if a username with the given name exists
     *
     * @param newUsername name of the new username
     * @return true if it exists and false if it doesn't exist
     */
    public boolean userExists(String newUsername) {
        PreparedStatement statement;
        try (Connection connection = databaseHandler.getConnection()) {
            try {
                statement = connection.prepareStatement(PreparedStatements.USER_EXISTS_SQL);
                statement.setString(1, newUsername);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }


    /**
     * Checks if the password has satisfies a set of requirements
     * such as lower case, upper case, digits and special characters
     *
     * @param password password that user entered
     * @return true if the password meets the requirements and false if not
     */
    public boolean isPassValid(String password) {
        if (password.length() < 8) {
            return false;
        }
        if (!password.matches("(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%]).{8,}")) {
            return false;
        }
        return true;
    }


    /**
     * Checks if the username and the password match the data in the database when users login
     *
     * @param username username
     * @param password password
     * @return true if password and username matched and exists and false otherwise
     */
    public boolean authenticateUser(String username, String password) {
        PreparedStatement statement;
        try (Connection connection = databaseHandler.getConnection()) {
            statement = connection.prepareStatement(PreparedStatements.AUTH_SQL);
            String usersalt = getSalt(connection, username);
            String passhash = getHash(password, usersalt);

            statement.setString(1, username);
            statement.setString(2, passhash);
            ResultSet results = statement.executeQuery();
            return results.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    /**
     * Gets the salt for a specific user.
     *
     * @param connection - active database connection
     * @param user       - which user to retrieve salt for
     * @return salt for the specified user or null if user does not exist
     */
    private String getSalt(Connection connection, String user) {
        String salt = null;
        try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.SALT_SQL)) {
            statement.setString(1, user);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                salt = results.getString("usersalt");
                return salt;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return salt;
    }


    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes  - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    private static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
     * Calculates the hash of a password and salt using SHA-256.
     *
     * @param password - password to hash
     * @param salt     - salt associated with user
     * @return hashed password
     */
    private static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return hashed;
    }

    /**
     * Retrieves the last login time of the specified user.
     *
     * @param username - the username to retrieve the last login time for
     * @return the last login time formatted as "h:mma, MM:dd:yyyy", or null if no login time is recorded
     */
    public String getLastLoginTime(String username) {
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_LAST_LOGIN_SQL)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Timestamp lastLogin = resultSet.getTimestamp("lastLogin");
                if (lastLogin != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mma, MM:dd:yyyy");
                    return sdf.format(lastLogin);
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Updates the last login time of the specified user.
     *
     * @param username  - the username to update the last login time for
     * @param loginTime - the formatted login time to set
     */
    public void updateLastLoginTime(String username, String loginTime) {
        try (Connection connection = databaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.UPDATE_LAST_LOGIN_SQL)) {
            statement.setString(1, loginTime);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
