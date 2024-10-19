package user.myapp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FineDAO {
    private Connection connection;

    public FineDAO(Connection connection) {
        this.connection = connection;
    }

    // Get all fines (you can filter by user ID if needed)
    public List<Fine> getAllFines() throws SQLException {
        String query = "SELECT * FROM fines";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet result = statement.executeQuery();

        List<Fine> fines = new ArrayList<>();
        while (result.next()) {
            Fine fine = new Fine();
            fine.setId(result.getInt("id"));
            fine.setUserId(result.getInt("user_id"));
            fine.setBookId(result.getInt("book_id"));
            fine.setFineAmount(result.getBigDecimal("fine_amount"));
            fine.setPaymentDate(result.getDate("payment_date"));
            fines.add(fine);
        }
        return fines;
    }
}
