package user.myapp;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                System.out.println("Connection successful!");
                conn.close();  // Close the connection after test
            } else {
                System.out.println("Failed to connect!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
