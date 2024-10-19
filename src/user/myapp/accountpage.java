package user.myapp;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class accountpage extends JFrame {
    private static final long serialVersionUID = 1L;
    private int userId;
    private JTable borrowedBooksTable;

    public accountpage(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        // Fetch and display borrowed books
        String[] columns = {"Book ID", "Borrow Date", "Return Date"};
        String[][] borrowedBooksData = fetchBorrowedBooksData();

        borrowedBooksTable = new JTable(borrowedBooksData, columns);
        add(new JScrollPane(borrowedBooksTable), BorderLayout.CENTER);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            new homepage(userId, userId).setVisible(true);
            dispose();
        });

        // View Fines Button
        JButton finesButton = new JButton("View Fines");
        finesButton.addActionListener(e -> viewFines());

        // Return Book Button
        JButton returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(e -> {
            new ReturnBookPage(userId).setVisible(true);  // Open the ReturnBookPage
            dispose();  // Close the account page
        });

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(finesButton);
        buttonPanel.add(returnBookButton);  // Add the Return Book button

        add(buttonPanel, BorderLayout.SOUTH);
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Fetch borrowed books data (with return date) from the database
    private String[][] fetchBorrowedBooksData() {
        ArrayList<String[]> borrowedBooksList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT book_id, borrow_date, return_date FROM borrowed_books WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String[] borrowedBook = {
                        String.valueOf(rs.getInt("book_id")),
                        rs.getDate("borrow_date") != null ? rs.getDate("borrow_date").toString() : "N/A",
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toString() : "N/A"
                };
                borrowedBooksList.add(borrowedBook);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching borrowed books: " + ex.getMessage());
        }
        return borrowedBooksList.toArray(new String[0][0]);
    }

    // Method to view fines
    private void viewFines() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT fine_amount FROM fines WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder finesList = new StringBuilder("Your Fines:\n");
            boolean hasFines = false;

            while (rs.next()) {
                hasFines = true;
                finesList.append("Fine: $").append(rs.getBigDecimal("fine_amount")).append("\n");
            }

            if (!hasFines) {
                finesList.append("No fines recorded.");
            }

            JOptionPane.showMessageDialog(this, finesList.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching fines: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        int loggedInUserId = 1;  // Example user ID
        new accountpage(loggedInUserId);  // Open the account page
    }
}



