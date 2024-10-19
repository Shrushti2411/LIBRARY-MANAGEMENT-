package user.myapp;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class catalogpage extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable catalogTable;
    private JTextField searchField;
    private JButton borrowButton, backButton, searchButton, ebookButton;
    private int userId;  // Store the user's ID
    private String[][] booksData;  // Store book data for current search

    public catalogpage(int userId) {  // Accept userId as a parameter
        this.userId = userId;  // Store the userId

        setLayout(new BorderLayout());

        // Search field and search button
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search Books:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);  // Add the search panel at the top

        // Columns for the table
        String[] columns = {"ID", "Title", "Author", "Availability", "eBook"};
        booksData = fetchBooksData("");  // Fetch all books initially
        catalogTable = new JTable(booksData, columns);

        // Borrow button
        borrowButton = new JButton("Borrow Book");
        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = catalogTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int bookId = Integer.parseInt((String) catalogTable.getValueAt(selectedRow, 0));
                    borrowBook(bookId);  // Call borrowBook method
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a book to borrow.");
                }
            }
        });

        // eBook button
        ebookButton = new JButton("Download eBook");
        ebookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = catalogTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String ebookUrl = booksData[selectedRow][4];  // Get eBook URL
                    if (ebookUrl != null && !ebookUrl.isEmpty()) {
                        openWebpage(ebookUrl);  // Open the eBook download link
                    } else {
                        JOptionPane.showMessageDialog(null, "No eBook available for this title.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a book to download the eBook.");
                }
            }
        });

        // Back button
        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new homepage(userId, userId).setVisible(true);  // Navigate back to the homepage
                dispose();  // Close the catalog page
            }
        });

        // Search button action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText();
                booksData = fetchBooksData(keyword);  // Fetch data based on search
                catalogTable.setModel(new javax.swing.table.DefaultTableModel(booksData, columns));  // Update table with search results
            }
        });

        // Add components to the layout
        add(new JScrollPane(catalogTable), BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(borrowButton);
        buttonsPanel.add(ebookButton);  // Add eBook button to the panel
        buttonsPanel.add(backButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Fetch books data from the database (with optional search keyword)
    private String[][] fetchBooksData(String keyword) {
        String[][] data = new String[10][5];  // Including space for eBook column
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM books WHERE title LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%");  // Search by title
            ResultSet rs = stmt.executeQuery();

            int row = 0;
            while (rs.next()) {
                data[row][0] = String.valueOf(rs.getInt("id"));
                data[row][1] = rs.getString("title");
                data[row][2] = rs.getString("author");
                data[row][3] = rs.getBoolean("availability") ? "Available" : "Not Available";
                data[row][4] = rs.getString("ebook_url") != null ? "Available" : "Not Available";  // Check if eBook is available
                row++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    // Borrow book method (updated with return date)
    private void borrowBook(int bookId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if book is available
            String checkQuery = "SELECT availability FROM books WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getBoolean("availability")) {
                // Book is available, mark it as borrowed
                String updateQuery = "UPDATE books SET availability = false WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();

                // Calculate return date (7 days from today)
                LocalDate borrowDate = LocalDate.now();
                LocalDate returnDate = borrowDate.plusDays(7);

                // Insert record into borrowed_books table with the return date
                String insertQuery = "INSERT INTO borrowed_books (user_id, book_id, borrow_date, return_date) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, userId);  // Use the stored userId
                insertStmt.setInt(2, bookId);
                insertStmt.setDate(3, java.sql.Date.valueOf(borrowDate));
                insertStmt.setDate(4, java.sql.Date.valueOf(returnDate));
                insertStmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Book borrowed successfully. Return by: " + returnDate);
            } else {
                JOptionPane.showMessageDialog(null, "Sorry, the book is not available.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Method to open the eBook download link in the browser
    private void openWebpage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to open the eBook link.");
        }
    }

    public static void main(String[] args) {
        int loggedInUserId = 1;  // Example user ID, modify as needed
        new catalogpage(loggedInUserId);  // Passing user ID to the catalog page
    }
}
