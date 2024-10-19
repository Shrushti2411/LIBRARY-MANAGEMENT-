package user.myapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection connection;

    // Constructor to establish the database connection
    public DatabaseManager(String url, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username , password);
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            connection = null;
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    // Search books in the database by title
    public List<Book> searchBooks(String query) {
        if (connection == null) {
            System.out.println("No database connection.");
            return new ArrayList<>();  // Return an empty list if connection is null
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM books WHERE title LIKE ?");
            statement.setString(1, "%" + query + "%");
            ResultSet result = statement.executeQuery();
            List<Book> books = new ArrayList<>();
            while (result.next()) {
                Book book = new Book();
                book.setTitle(result.getString("title"));
                book.setAuthor(result.getString("author"));
                books.add(book);
            }
            return books;
        } catch (SQLException e) {
            System.out.println("Error searching books: " + e.getMessage());
            return new ArrayList<>();  // Return an empty list on error
        }
    }

    // Close the database connection
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    // Main method to run the database manager
    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager("jdbc:mysql://localhost:3306/library", "root", "s1h2r3u4@2511");

        List<Book> books = databaseManager.searchBooks("java");
        for (Book book : books) {
            System.out.println(book.getTitle() + " by " + book.getAuthor());
        }

        databaseManager.closeConnection();
    }
}