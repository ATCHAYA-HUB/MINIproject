package movieticket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookSeats {
    private String url;
    private String user;
    private String password;

    public BookSeats(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
public static boolean flag=true;
    public void bookSeats(String movieName, String userId, List<String> seats) {
        String movieId = getMovieId(movieName);
        if (movieId == null) {
            System.out.println("Movie not found. Booking seats failed.");
            return;
        }

        List<List<String>> bookedSeats = getBookedSeats(movieName);
        if (bookedSeats == null) {
            bookedSeats = new ArrayList<>();
        }

        List<String> invalidSeats = new ArrayList<>();
        List<String> alreadyBookedSeats = new ArrayList<>();

        // Check if the entered seats are valid and not already booked
        for (String seat : seats) {
            if (!isValidSeat(seat)) {
                invalidSeats.add(seat);
            } else if (isSeatBooked(seat, bookedSeats)) {
                alreadyBookedSeats.add(seat);
            }
        }

        if (!invalidSeats.isEmpty() || !alreadyBookedSeats.isEmpty()) {
            System.out.println("Booking seats failed. The following seats are:");
            flag=false;
            if (!invalidSeats.isEmpty()) {
                System.out.println("Invalid seats: " + String.join(", ", invalidSeats));
                flag=false;
            }

            if (!alreadyBookedSeats.isEmpty()) {
                System.out.println("Already booked seats: " + String.join(", ", alreadyBookedSeats));
                flag=false;
            }
            return;
        }

        // Add the booked seats to the list of booked seats
        bookedSeats.add(seats);

        // Update the booked seats for the movie in the database
        updateBookedSeats(movieName, bookedSeats, movieId, userId);
    }

    public void displayBookedSeats(String movieName) {
        List<List<String>> bookedSeats = getBookedSeats(movieName);
        if (bookedSeats == null || bookedSeats.isEmpty()) {
            System.out.println("No seats are booked for the movie: " + movieName);
            return;
        }

        System.out.println("Booked seats for the movie " + movieName + ",");
        for (List<String> seatsList : bookedSeats) {
            System.out.println(String.join(", ", seatsList));
        }
    }

    private String getMovieId(String movieName) {
        String movieId = null;
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT movie_id FROM movie WHERE title = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, movieName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                movieId = resultSet.getString("movie_id");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching movie ID: " + e.getMessage());
        }
        return movieId;
    }

    private List<List<String>> getBookedSeats(String movieName) {
        List<List<String>> bookedSeats = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT booked_seats FROM booked_seats WHERE movie_name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, movieName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String bookedSeatsStr = resultSet.getString("booked_seats");
                String[] seatsArray = bookedSeatsStr.split(",");
                for (String seatsStr : seatsArray) {
                    List<String> seatsList = Arrays.asList(seatsStr.split(","));
                    bookedSeats.add(seatsList);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching booked seats: " + e.getMessage());
        }
        return bookedSeats;
    }



    private boolean isValidSeat(String seat) {
        // Add your validation logic here, e.g., checking if the seat is within the valid range.
        // For simplicity, let's assume all seats are valid for this example.
        return true;
    }

    private boolean isSeatBooked(String seat, List<List<String>> bookedSeats) {
        // Check if the seat is already booked
        for (List<String> seatsList : bookedSeats) {
            if (seatsList.contains(seat)) {
                return true;
            }
        }
        return false;
    }

    private void updateBookedSeats(String movieName, List<List<String>> bookedSeats, String movieId, String userId) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String selectSql = "SELECT * FROM booked_seats WHERE movie_name = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, movieName);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Movie already has booked seats, update the existing row
                String updateSql = "UPDATE booked_seats SET booked_seats = ?, user_id = ? WHERE movie_name = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, convertToBookedSeatsString(bookedSeats));
                updateStatement.setString(2, userId);
                updateStatement.setString(3, movieName);
                updateStatement.executeUpdate();
                System.out.println("Seats booked successfully for movie: " + movieName);
            } else {
                // Movie does not have booked seats, insert a new row
                String insertSql = "INSERT INTO booked_seats (movie_name, movie_id, booked_seats, user_id) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                insertStatement.setString(1, movieName);
                insertStatement.setString(2, movieId);
                insertStatement.setString(3, convertToBookedSeatsString(bookedSeats));
                insertStatement.setString(4, userId);
                insertStatement.executeUpdate();
                System.out.println("Seats booked successfully for movie: " + movieName);
            }
        } catch (SQLException e) {
            System.err.println("Error updating booked seats: " + e.getMessage());
        }
    }


    private String convertToBookedSeatsString(List<List<String>> bookedSeats) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bookedSeats.size(); i++) {
            List<String> seatsList = bookedSeats.get(i);
            sb.append(String.join(",", seatsList));
            if (i < bookedSeats.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
