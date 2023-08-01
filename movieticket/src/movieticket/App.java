package movieticket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class App {
	 public static void main(String[] args) throws SQLException {
	        System.out.println("Welcome to Movie Ticket Booking App");
	        Scanner scanner = new Scanner(System.in);
	        String url = "jdbc:mysql://localhost:3306/movieticket";
	        String user = "root";
	        String password = "@atchaya22";

	        BookSeats bookSeats = new BookSeats(url, user, password);

	        while (true) {
	            System.out.println("\nMain Menu:");
	            System.out.println("1. Create User");
	            System.out.println("2. Search Movie");
	            System.out.println("3. Book Seats");
	            System.out.print("Please enter your choice: ");
	            int choice = scanner.nextInt();
	            scanner.nextLine();
	            switch (choice) {
	                case 1:
	                    createUser(scanner);
	                    break;
	                case 2:
	                    searchMovies(scanner);
	                    break;
	                case 3:
	                    bookSeats(scanner,bookSeats);
	                    break;
	                default:
	                    System.out.println("Invalid choice. Please try again.");
	            }
	        }
	    }
    public static void createUser(Scanner scanner) {
        String url = "jdbc:mysql://localhost:3306/movieticket";
        String user = "root";
        String password = "@atchaya22";
        System.out.println("Enter user ID:");
        String userId = scanner.nextLine();
        System.out.println("Enter user name:");
        String name = scanner.nextLine();
        System.out.println("Enter user email:");
        String email = scanner.nextLine();

        UserJdbcDao userJdbcDao = new UserJdbcDao(url, user, password);

        User user2 = new User(userId, name, email);

        userJdbcDao.addUser(user2);
    }
    public static void searchMovies(Scanner scanner) {

    	System.out.print("Enter movie title to search: ");
        String title = scanner.nextLine();

        List<Movie> matchedMovies = Movie.searchMovieByTitle(title);

        if (matchedMovies.isEmpty()) {
            System.out.println("No movies found matching the search term.");
            return;
        }

        System.out.println("\nSearch Results:");
        for (Movie movie : matchedMovies) {
            System.out.println("Movie ID: " + movie.getMovieId());
            System.out.println("Title: " + movie.getTitle());
            System.out.println("Genre: " + movie.getGenre());
            System.out.println("Duration: " + movie.getDuration() + " minutes");
            System.out.println("Price: "+movie.getPrice()+" rupees");
            System.out.println();
        }
    }
    public static float totamt=0;
    public static void bookSeats(Scanner scanner, BookSeats bookSeats) throws SQLException {
        System.out.print("Enter movie name: ");
        String movieName = scanner.nextLine();
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/movieticket", "root","@atchaya22");
            String sql = "SELECT * FROM movie where title = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,movieName);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String movieTitle = resultSet.getString("title");
                String genre = resultSet.getString("genre");
                String duration = resultSet.getString("duration");
                float price=resultSet.getFloat("price");
                totamt=price;
                System.out.println(movieTitle+" Genre("+genre+") Duration :"+duration+" minutes Price : "+price);
            }
            String sql1 = "SELECT * FROM booked_seats where movie_name = ?";
            PreparedStatement statement1 = connection.prepareStatement(sql1);
            statement1.setString(1,movieName);

            ResultSet resultSet1 = statement1.executeQuery();
            while (resultSet1.next()) {
                String bookedseats = resultSet1.getString("booked_seats");
                System.out.println("Booked Seats (Unavailable) : "+bookedseats);
            }
            
        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine();

        System.out.print("Enter seats to book (comma-separated): ");
        String[] seatsArray = scanner.nextLine().split(",");
        List<String> seatsToBook = List.of(seatsArray);
        List<String> seats = Arrays.asList(seatsArray);
        int ticketCount = seats.size();
        bookSeats.bookSeats(movieName, userId, seatsToBook);
        if(BookSeats.flag==true) {
        	System.out.println("No of Tickets : "+ticketCount+"\nPrice : "+(totamt*ticketCount));
        	System.out.println("Confirm Ticket Booking (YES or NO) : ");
        	String booking = scanner.nextLine();
            if(booking.equals("YES")) {
            System.out.println("Tickets booked: " + ticketCount+" for "+(totamt*ticketCount)+" rupees");
            }
            else {
            	System.out.println("Cancelled");
            }
        }
        
    }
}
