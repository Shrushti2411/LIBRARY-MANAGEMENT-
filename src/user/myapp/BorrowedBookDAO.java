package user.myapp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BorrowedBookDAO {
    private Connection connection;

    public BorrowedBookDAO(Connection connection) {
        this.connection = connection;
    }

    // Get all borrowed books (you can filter by user ID if needed)
    public List<BorrowedBook> getAllBorrowedBooks() throws SQLException {
        String query = "SELECT * FROM borrowed_books";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet result = statement.executeQuery();

        List<BorrowedBook> borrowedBooks = new ArrayList<>();
        while (result.next()) {
            BorrowedBook borrowedBook = new BorrowedBook();
            borrowedBook.setId(result.getInt("id"));
            borrowedBook.setUserId(result.getInt("user_id"));
            borrowedBook.setBookId(result.getInt("book_id"));
            borrowedBook.setBorrowDate(result.getDate("borrow_date"));
            borrowedBook.setReturnDate(result.getDate("return_date"));
            borrowedBooks.add(borrowedBook);
        }
        return borrowedBooks;
    }
}