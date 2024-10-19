package user.myapp;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class adminPage extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private int adminId;

    public adminPage(int adminId) {
        this.setAdminId(adminId);  // Store adminId for use when navigating
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, Admin (Admin ID: " + adminId + ")");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        // Buttons to navigate to different sections
        JPanel buttonsPanel = new JPanel();
        JButton addBookButton = new JButton("Add Book");
        JButton chargeFineButton = new JButton("Charge Fine");
        JButton logoutButton = new JButton("Logout");

        buttonsPanel.add(addBookButton);
        buttonsPanel.add(chargeFineButton);
        buttonsPanel.add(logoutButton);
        add(buttonsPanel, BorderLayout.CENTER);

        // Action listeners for buttons
        addBookButton.addActionListener(_ -> {
            new addBookPage(adminId).setVisible(true);  // Navigate to add book page
            dispose();
        });

        chargeFineButton.addActionListener(_ -> {
            new chargeFinePage(adminId).setVisible(true);  // Navigate to charge fine page
            dispose();
        });

        logoutButton.addActionListener(_ -> {
            new loginpage().setVisible(true);  // Redirect to login page
            dispose();  //
        });

        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
}

