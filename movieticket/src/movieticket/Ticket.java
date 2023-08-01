package movieticket;
import java.util.HashSet;
import java.util.Set;
abstract class ticketab {
	abstract public void display();
}
public class Ticket extends ticketab implements Displayable {
    private String ticketId;
    private Movie movie;
    private User user;
    private String showTime;
    private String[] seats;
    private static Set<String> bookedSeats = new HashSet<>();

    public Ticket(String ticketId, Movie movie, User user, String showTime, String[] seats) {
        this.ticketId = ticketId;
        this.movie = movie;
        this.user = user;
        this.showTime = showTime;
        this.seats = seats;
        for (String seat : seats) {
            if (isSeatAvailable(seat)) {
                bookedSeats.add(seat);
            } else {
                System.out.println("Seat " + seat + " has already been purchased for this show time.");
            }
        }
    }

    public void displayTicketInfo() {
        System.out.println("Ticket ID: " + ticketId);
        System.out.println("Movie Information:");
        movie.displayMovieInfo();
        System.out.println("User Information:");
        user.displayUserInfo();
        System.out.println("Show Time: " + showTime);
        System.out.println("Seats: " + String.join(", ", seats));
    }

    @Override
    public void display() {
        displayTicketInfo();
    }

    public static void displayBookedSeats(String showTime) {
        System.out.println("Booked Seats for Show Time: " + showTime);
        if (bookedSeats.isEmpty()) {
            System.out.println("No seats booked for this show time.");
        } else {
            System.out.println("Booked Seat Numbers: " + String.join(", ", bookedSeats));
        }
    }

    public static boolean isSeatAvailable(String seat) {
        return !bookedSeats.contains(seat);
    }
}
