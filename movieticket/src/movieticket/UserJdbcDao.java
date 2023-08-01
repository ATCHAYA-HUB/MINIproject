package movieticket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserJdbcDao {
    private String url;
    private String user;
    private String password;

    public UserJdbcDao(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

	// Create a new user in the database
    public void addUser(User user) {
        String sqlSelect = "SELECT COUNT(*) AS count FROM user WHERE user_id = ?";
        String sqlInsert = "INSERT INTO user (user_id, name, email) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, this.user, this.password);
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect);
             PreparedStatement insertStatement = connection.prepareStatement(sqlInsert)) {

            // Check if the user ID already exists
            selectStatement.setString(1, user.getUserId());
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                if (count > 0) {
                    System.out.println("User ID already exists. User not added.");
                    return;
                }
            }

            // Insert the new user if the user ID does not exist
            insertStatement.setString(1, user.getUserId());
            insertStatement.setString(2, user.getName());
            insertStatement.setString(3, user.getEmail());

            int rowsInserted = insertStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User added successfully.");
            }

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }
}
