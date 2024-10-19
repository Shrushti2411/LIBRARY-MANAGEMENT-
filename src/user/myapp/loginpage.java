package user.myapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class loginpage extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public loginpage() {
        setLayout(new BorderLayout());

        // Username and password input fields
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2));
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        fieldsPanel.add(usernameLabel);
        fieldsPanel.add(usernameField);
        fieldsPanel.add(passwordLabel);
        fieldsPanel.add(passwordField);

        // Login button
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        buttonsPanel.add(loginButton);

        // ActionListener for the Login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try (Connection conn = DatabaseConnection.getConnection()) {
                    UserDAO userDAO = new UserDAO(conn);  // Initialize UserDAO with the connection
                    User user = userDAO.getUserByUsername(username);

                    // Validate user credentials
                    if (user != null && user.getPassword().equals(password)) {
                        JOptionPane.showMessageDialog(null, "Login successful!");

                        // Check if the user is an admin or student based on their role
                        if (user.getRole().equalsIgnoreCase("admin")) {
                            System.out.println("Redirecting to admin page");
                            new adminPage(user.getId()).setVisible(true);  // Redirect to admin page
                        } else {
                            System.out.println("Redirecting to homepage");
                            new homepage(user.getId(), user.getId()).setVisible(true);  // Redirect to student homepage
                        }
                        dispose();  // Close login window
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new loginpage();
    }
}
