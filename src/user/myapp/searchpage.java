package user.myapp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class searchpage extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField searchField;
    private JTextArea resultArea;

    public searchpage() {
        setLayout(new BorderLayout());

        // Create panel for search field
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Create panel for result area
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Add panels to frame
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ActionListener for search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText();
                String result = searchBooks(query);
                resultArea.setText(result);
            }
        });

        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private String searchBooks(String query) {
        String result = "";

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM books WHERE title LIKE ?";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                result += "Title: " + rs.getString("title") + "\n";
                result += "Author: " + rs.getString("author") + "\n";
                result += "Availability: " + rs.getBoolean("availability") + "\n\n";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {
        new searchpage();
    }
}
