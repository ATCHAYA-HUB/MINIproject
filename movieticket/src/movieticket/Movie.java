package movieticket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class Movie implements Displayable{
    private String movieId;
    private String title;
    private String genre;
    private String duration;
    private float price;
    public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getMovieId() {
		return movieId;
	}

	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Movie(String movieId, String title, String genre, String duration,float price) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.price=price;
    }

    public void displayMovieInfo() {
        System.out.println("Movie ID: " + movieId);
        System.out.println("Title: " + title);
        System.out.println("Genre: " + genre);
        System.out.println("Duration: " + duration + " minutes");
        System.out.println("Price: "+price+" rupees");
    }
    @Override
    public void display() {
        displayMovieInfo();
    }
    public static List<Movie> searchMovieByTitle(String title) {
        String url = "jdbc:mysql://localhost:3306/movieticket";
        String user = "root";
        String password = "@atchaya22";

        List<Movie> matchedMovies = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM movie WHERE title LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + title + "%");

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String movieId = resultSet.getString("movie_id");
                String movieTitle = resultSet.getString("title");
                String genre = resultSet.getString("genre");
                String duration = resultSet.getString("duration");
                float price=resultSet.getFloat("price");
                Movie movie = new Movie(movieId, movieTitle, genre, duration,price);
                matchedMovies.add(movie);
            }
        } catch (SQLException e) {
            System.err.println("Error searching for movies: " + e.getMessage());
        }

        return matchedMovies;
    }
    
}

