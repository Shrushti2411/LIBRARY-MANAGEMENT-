package user.myapp;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReturnBookPage extends JFrame {
    private static final long serialVersionUID = 1L;
    private int userId;
    private JTable borrowedBooksTable;
    private JButton returnButton, backButton;
    private String[][] borrowedBooksData;

    public ReturnBookPage(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        // Fetch and display the list of borrowed books
        String[] columns = {"ID", "Title", "Borrow Date", "Return Date", "Status"};
        borrowedBooksData = fetchBorrowedBooksData();  // Fetch all borrowed books for the user
        borrowedBooksTable = new JTable(borrowedBooksData, columns);
        add(new JScrollPane(borrowedBooksTable), BorderLayout.CENTER);

        // Return button
        returnButton = new JButton("Return Book");
        returnButton.addActionListener(e -> {
            int selectedRow = borrowedBooksTable.getSelectedRow();
            if (selectedRow >= 0) {
                int bookId = Integer.parseInt(borrowedBooksData[selectedRow][0]);
                returnBook(bookId);  // Call returnBook method
            } else {
                JOptionPane.showMessageDialog(null, "Please select a book to return.");
            }
        });

        // Back button
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            new homepage(userId, userId).setVisible(true);  // Navigate back to the homepage
            dispose();
        });

        // Add buttons to the layout
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(returnButton);
        buttonsPanel.add(backButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Fetch borrowed books for the user
    private String[][] fetchBorrowedBooksData() {
        java.util.List<String[]> data = new java.util.ArrayList<>();  // Use ArrayList to handle dynamic sizing
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT bb.book_id, b.title, bb.borrow_date, bb.return_date " +
                    "FROM borrowed_books bb " +
                    "JOIN books b ON bb.book_id = b.id " +
                    "WHERE bb.user_id = ? AND bb.actual_return_date IS NULL";  // Only fetch books that haven't been returned
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String[] row = new String[5]; // Assuming you want to include the status too
                row[0] = String.valueOf(rs.getInt("book_id"));
                row[1] = rs.getString("title");
                row[2] = rs.getDate("borrow_date").toString();
                row[3] = rs.getDate("return_date").toString();
                row[4] = "Not Returned";  // Status of the book
                data.add(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching borrowed books: " + ex.getMessage());
        }
        // Convert ArrayList to String[][]
        return data.toArray(new String[0][0]);
    }

    // Method to return a book
    private void returnBook(int bookId) {
        LocalDate expectedReturnDate = null; // To store the expected return date
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Fetch the expected return date first
            String query = "SELECT return_date FROM borrowed_books WHERE user_id = ? AND book_id = ?";
            PreparedStatement fetchDateStmt = conn.prepareStatement(query);
            fetchDateStmt.setInt(1, userId);
            fetchDateStmt.setInt(2, bookId);
            ResultSet rs = fetchDateStmt.executeQuery();

            if (rs.next()) {
                expectedReturnDate = rs.getDate("return_date").toLocalDate();
            }

            // Update the borrowed_books table to set actual_return_date
            String updateBorrowedQuery = "UPDATE borrowed_books SET actual_return_date = ? WHERE user_id = ? AND book_id = ?";
            PreparedStatement updateBorrowedStmt = conn.prepareStatement(updateBorrowedQuery);
            updateBorrowedStmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));  // Set today's date as return date
            updateBorrowedStmt.setInt(2, userId);
            updateBorrowedStmt.setInt(3, bookId);
            updateBorrowedStmt.executeUpdate();

            // Update the books table to set the availability back to true
            String updateBookQuery = "UPDATE books SET availability = true WHERE id = ?";
            PreparedStatement updateBookStmt = conn.prepareStatement(updateBookQuery);
            updateBookStmt.setInt(1, bookId);
            updateBookStmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Book returned successfully.");

            // Check if the book is returned late and add a fine
            LocalDate actualReturnDate = LocalDate.now();  // Assume book is returned today

            if (expectedReturnDate != null && actualReturnDate.isAfter(expectedReturnDate)) {
                long daysLate = java.time.temporal.ChronoUnit.DAYS.between(expectedReturnDate, actualReturnDate);
                BigDecimal fineAmount = BigDecimal.valueOf(daysLate * 1.50);  // Example: $1.50 per day late

                // Insert fine into the fines table
                String fineQuery = "INSERT INTO fines (user_id, book_id, fine_amount) VALUES (?, ?, ?)";
                PreparedStatement fineStmt = conn.prepareStatement(fineQuery);
                fineStmt.setInt(1, userId);
                fineStmt.setInt(2, bookId);
                fineStmt.setBigDecimal(3, fineAmount);
                fineStmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Book returned late. Fine added: $" + fineAmount);
            }

            // Reload the data to reflect the changes
            borrowedBooksData = fetchBorrowedBooksData();
            borrowedBooksTable.setModel(new javax.swing.table.DefaultTableModel(borrowedBooksData, new String[]{"ID", "Title", "Borrow Date", "Return Date", "Status"}));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error returning book: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        int loggedInUserId = 1;  // Example user ID
        new ReturnBookPage(loggedInUserId);  // Open the return book page
    }
}

