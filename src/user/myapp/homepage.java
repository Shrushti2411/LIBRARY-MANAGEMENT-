package user.myapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class homepage extends JFrame {
    private static final long serialVersionUID = 1L;
    public int userId;

    public homepage(int userId, int adminId)  {
        this.userId = userId;  // Store userId for use when navigating to other pages
        setLayout(new BorderLayout());

        // Welcome label (You can customize this with the user’s name if available)
        JLabel welcomeLabel = new JLabel("Welcome to the Library Management System");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        // Buttons to navigate to different sections
        JPanel buttonsPanel = new JPanel();
        JButton catalogButton = new JButton("Catalog");
        JButton accountButton = new JButton("Account");
        JButton logoutButton = new JButton("Logout");

        buttonsPanel.add(catalogButton);
        buttonsPanel.add(accountButton);
        buttonsPanel.add(logoutButton);

        add(buttonsPanel, BorderLayout.CENTER);

        // Action listener for Catalog Button
        catalogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new catalogpage(userId).setVisible(true);  // Navigate to catalog page
                dispose();  // Close homepage
            }
        });


        // Action listener for Account Button
        accountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new accountpage(userId).setVisible(true);  // Pass userId to accountpage and open it
                dispose();  // Close homepage
            }
        });

        // Action listener for Logout Button
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new loginpage().setVisible(true);  // Redirect to login page
                dispose();  // Close homepage
            }
        });

        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the frame on the screen
        setVisible(true);
    }

    public homepage() {
        // Default constructor
    }

    public static void main(String[] args) {
        // Example: When creating a new homepage, pass the user ID obtained during login.
        int loggedInUserId = 1;  // Replace with the actual logged-in user’s ID
        new homepage(loggedInUserId, loggedInUserId);
    }
}
