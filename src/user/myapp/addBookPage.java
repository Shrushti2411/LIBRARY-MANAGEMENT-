package user.myapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class addBookPage extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JTextField titleField, authorField, ebookUrlField;
    private JButton addButton, backButton;

    public addBookPage(int adminId) {
        setLayout(new FlowLayout()); // Reverting to original layout

        // Labels and text fields
        add(new JLabel("Title:"));
        titleField = new JTextField(20);
        add(titleField);

        add(new JLabel("Author:"));
        authorField = new JTextField(20);
        add(authorField);

        // Add new label and text field for eBook URL
        add(new JLabel("eBook URL:"));
        ebookUrlField = new JTextField(20);
        add(ebookUrlField);

        // Add and back buttons
        addButton = new JButton("Add Book");
        addButton.addActionListener(this);  // Set action listener for Add button
        add(addButton);

        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            new adminPage(adminId); // Navigate back to admin page
            dispose();
        });
        add(backButton);

        setSize(300, 250);  // Reverting the size back to the original size
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String ebookUrl = ebookUrlField.getText().trim();

            // Validate input: Ensure title and author are not empty
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and Author fields cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Load the MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Create a connection to the database
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "s1h2r3u4@2511");

                // Create a prepared statement to insert the book data, including the eBook URL
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO books (title, author, availability, ebook_url) VALUES (?, ?, ?, ?)");
                pstmt.setString(1, title);
                pstmt.setString(2, author);
                pstmt.setBoolean(3, true);  // Set availability to true (available)
                pstmt.setString(4, ebookUrl.isEmpty() ? null : ebookUrl);  // Set eBook URL if provided, otherwise null

                // Execute the prepared statement
                int rowsAffected = pstmt.executeUpdate();

                // Close the connection
                conn.close();

                // If insertion is successful, show success message
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Book added successfully!");

                    // Clear the fields
                    titleField.setText("");
                    authorField.setText("");
                    ebookUrlField.setText("");  // Clear the eBook URL field
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add the book!", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Driver not found: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new addBookPage(1);  // Test the page with example adminId 1
    }
}
