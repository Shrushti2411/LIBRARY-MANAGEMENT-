package user.myapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupPage extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JLabel usernameLabel, passwordLabel, roleLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton signupButton, loginButton;

    public SignupPage() {
        setLayout(new FlowLayout());

        // Username label and field
        usernameLabel = new JLabel("Username:");
        add(usernameLabel);

        usernameField = new JTextField(20);
        add(usernameField);

        // Password label and field
        passwordLabel = new JLabel("Password:");
        add(passwordLabel);

        passwordField = new JPasswordField(20);
        add(passwordField);

        // Role label and dropdown
        roleLabel = new JLabel("Role:");
        add(roleLabel);

        String[] roles = { "admin", "student" };
        roleComboBox = new JComboBox<>(roles);
        add(roleComboBox);

        // Signup and login buttons
        signupButton = new JButton("Signup");
        signupButton.addActionListener(this);
        add(signupButton);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        add(loginButton);

        setSize(300, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signupButton) {
            System.out.println("Signup button clicked");

            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = roleComboBox.getSelectedItem().toString();

            if (username.isEmpty() || password.isEmpty() || role == null) {
                JOptionPane.showMessageDialog(this, "All fields must be filled!");
                return;
            }

            try {
                // Load the MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Create a connection to the database
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "s1h2r3u4@2511");

                // Create a prepared statement to insert the user data with the role
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, role);

                // Execute the prepared statement
                System.out.println("Inserting into database: " + username + ", " + role);
                pstmt.executeUpdate();
                System.out.println("Insertion successful");

                // Close the connection
                conn.close();

                JOptionPane.showMessageDialog(this, "Signup successful!");
                new loginpage();
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error inserting into database: " + ex.getMessage());
                ex.printStackTrace();
            } catch (ClassNotFoundException e1) {
                JOptionPane.showMessageDialog(this, "Driver not found: " + e1.getMessage());
                e1.printStackTrace();
            }
        } else if (e.getSource() == loginButton) {
            new loginpage();
            dispose();
        }
    }

    public static void main(String[] args) {
        new SignupPage();
    }
}
