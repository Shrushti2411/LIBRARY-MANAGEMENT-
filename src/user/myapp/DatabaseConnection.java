package user.myapp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    Connection conn = null;
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "root";
    private static final String PASSWORD = "s1h2r3u4@2511";


    // Method to get the connection
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Load MySQL JDBC driver
        } catch (ClassNotFoundException e) {
            /* Suppress warning about robust logging */
            e.printStackTrace();
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);  // Create the connection
    }
}

