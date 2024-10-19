package user.myapp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private Connection connection;  // Database connection

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setRole(resultSet.getString("role"));  // Set role from database
            return user;
        }
        return null;  // User not found
    }


    // Method to create a new user (for signup)
    public void createUser(User user) throws SQLException {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.executeUpdate();  // Execute the update query to insert the user
    }

    // Method to update user details (optional)
    public void updateUser(User user) throws SQLException {
        String query = "UPDATE users SET password = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, user.getPassword());
        statement.setInt(2, user.getId());
        statement.executeUpdate();
    }
}
