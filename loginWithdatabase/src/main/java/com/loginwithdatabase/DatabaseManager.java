package com.loginwithdatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/user_management";
    private static final String USER = "root"; // Default XAMPP username
    private static final String PASSWORD = ""; // Default XAMPP password

    // Fallback URL to create database if it doesn't exist
    private static final String JDBC_URL_WITHOUT_DB = "jdbc:mysql://localhost:3306/";

    /**
     * Establishes a connection to the MySQL database.
     * @return A Connection object if successful, otherwise null.
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // First try to connect to the specific database
            conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("Connection to MySQL database 'user_management' established.");

        } catch (SQLException e) {
            System.err.println("Could not connect to 'user_management' database: " + e.getMessage());

            // Try to create the database if it doesn't exist
            try {
                System.out.println("Attempting to create database 'user_management'...");
                createDatabase();
                // Try connecting again
                conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                System.out.println("Database created and connection established.");

            } catch (SQLException | ClassNotFoundException createEx) {
                System.err.println("Failed to create database: " + createEx.getMessage());
                throw new RuntimeException("Database connection failed", createEx);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
        return conn;
    }

    /**
     * Creates the user_management database if it doesn't exist
     */
    private static void createDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(JDBC_URL_WITHOUT_DB, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            String createDbSql = "CREATE DATABASE IF NOT EXISTS user_management";
            stmt.executeUpdate(createDbSql);
            System.out.println("Database 'user_management' created or already exists.");
        }
    }

    /**
     * Creates the 'users' table if it does not already exist.
     * This method should be called once, for example, at the application's startup.
     */
    public static void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "user_type VARCHAR(50) NOT NULL,"
                + "name VARCHAR(255) NOT NULL,"
                + "phone VARCHAR(20) NOT NULL,"
                + "username VARCHAR(50) NOT NULL UNIQUE,"
                + "password VARCHAR(255) NOT NULL,"
                + "location VARCHAR(255),"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";

        try (Connection conn = connect()) {
            if (conn != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.execute();
                    System.out.println("Users table created or already exists.");
                }
            } else {
                throw new SQLException("Could not establish database connection");
            }
        } catch (SQLException e) {
            System.err.println("Error creating users table: " + e.getMessage());
            throw new RuntimeException("Failed to create users table", e);
        }
    }

    /**
     * Tests if the database connection is working
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = connect()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a new user into the 'users' table.
     * @param userType The type of user (Student or House Owner).
     * @param name The user's full name.
     * @param phone The user's phone number.
     * @param username The user's chosen username.
     * @param password The user's password.
     * @param location The house owner's location (can be null for students).
     * @return true if the registration was successful, false otherwise.
     */
    public static boolean registerUser(String userType, String name, String phone, String username, String password, String location) {
        // NOTE: For a real application, you should hash the password before storing it.
        String sql = "INSERT INTO users(user_type, name, phone, username, password, location) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect()) {
            if (conn != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, userType);
                    pstmt.setString(2, name);
                    pstmt.setString(3, phone);
                    pstmt.setString(4, username);
                    pstmt.setString(5, password);
                    pstmt.setString(6, location);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("User registered successfully.");
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error
                System.err.println("Username already exists: " + username);
            } else {
                System.err.println("Error during user registration: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Unexpected error during registration: " + e.getMessage());
        }
        return false;
    }

    /**
     * Validates a user's login credentials against the database.
     * @param username The username to check.
     * @param password The password to check.
     * @return true if the credentials are valid, false otherwise.
     */
    public static boolean loginUser(String username, String password) {
        String sql = "SELECT id, user_type, name FROM users WHERE username = ? AND password = ?";

        try (Connection conn = connect()) {
            if (conn != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password); // NOTE: In a real app, you would verify the hashed password.

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("Login successful for user: " + rs.getString("name"));
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
        }

        System.out.println("Invalid username or password.");
        return false;
    }

    /**
     * Gets user information by username
     * @param username The username to look up
     * @return User information or null if not found
     */
    public static String getUserInfo(String username) {
        String sql = "SELECT user_type, name, phone, location FROM users WHERE username = ?";

        try (Connection conn = connect()) {
            if (conn != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            return String.format("User: %s (%s) - Phone: %s - Location: %s",
                                    rs.getString("name"),
                                    rs.getString("user_type"),
                                    rs.getString("phone"),
                                    rs.getString("location"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user info: " + e.getMessage());
        }

        return null;
    }
}