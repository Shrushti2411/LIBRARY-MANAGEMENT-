package user.myapp;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.math.BigDecimal;

public class chargeFinePage extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField userIdField, bookIdField, fineAmountField;
    private JButton chargeFineButton, backButton;
    private int adminId;

    public chargeFinePage(int adminId) {
        this.setAdminId(adminId);
        setLayout(new GridLayout(4, 2));

        // Fields for charging fines
        JLabel userIdLabel = new JLabel("User ID:");
        userIdField = new JTextField(5);
        JLabel bookIdLabel = new JLabel("Book ID:");
        bookIdField = new JTextField(5);
        JLabel fineAmountLabel = new JLabel("Fine Amount:");
        fineAmountField = new JTextField(10);

        chargeFineButton = new JButton("Charge Fine");
        chargeFineButton.addActionListener(e -> chargeFine());

        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            new adminPage(adminId).setVisible(true);
            dispose();
        });

        // Add components to the frame
        add(userIdLabel);
        add(userIdField);
        add(bookIdLabel);
        add(bookIdField);
        add(fineAmountLabel);
        add(fineAmountField);
        add(chargeFineButton);
        add(backButton);

        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void chargeFine() {
        String userIdStr = userIdField.getText();
        String bookIdStr = bookIdField.getText();
        String fineAmountStr = fineAmountField.getText();

        if (userIdStr.isEmpty() || bookIdStr.isEmpty() || fineAmountStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO fines (user_id, book_id, fine_amount) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(userIdStr));
            stmt.setInt(2, Integer.parseInt(bookIdStr));
            stmt.setBigDecimal(3, new BigDecimal(fineAmountStr));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Fine charged successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error charging fine.");
        }
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
}
