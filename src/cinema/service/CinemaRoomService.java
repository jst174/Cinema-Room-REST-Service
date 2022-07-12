package cinema.service;

import cinema.config.CinemaRoomConfigProperties;
import cinema.dto.Statistics;
import cinema.exception.SeatNotAvailableException;
import cinema.exception.SeatNotFoundException;
import cinema.exception.TicketNotFoundException;
import cinema.model.Seat;
import cinema.model.Ticket;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CinemaRoomService {

    private final CinemaRoomConfigProperties cinemaRoomConfigProperties;
    private final Map<Seat, Ticket> availableSeats;
    private final Map<String, Ticket> purchasedTickets;
    private final List<Seat> purchasedSeats;
    private final Statistics statistics;

    public CinemaRoomService(CinemaRoomConfigProperties cinemaRoomConfigProperties, Statistics statistics) {
        this.cinemaRoomConfigProperties = cinemaRoomConfigProperties;
        this.statistics = statistics;
        this.purchasedSeats = new ArrayList<>();
        this.purchasedTickets = new HashMap<>();
        this.availableSeats = new LinkedHashMap<>();
        fillingRoom();
    }

    public int getTotalRows() {
        return cinemaRoomConfigProperties.getTotalRows();
    }

    public int getTotalColumns() {
        return cinemaRoomConfigProperties.getTotalColumns();
    }

    public List<Ticket> getAvailableTicket() {
        return new ArrayList<>(availableSeats.values());
    }

    public List<Seat> getAvailableSeats() {
        return new ArrayList<>(availableSeats.keySet());
    }

    public List<Seat> getPurchasedSeats() {
        return purchasedSeats;
    }

    public Ticket findTicketByToken(String token) {
        if (!purchasedTickets.containsKey(token)) {
            throw new TicketNotFoundException("Wrong token!");
        }
        return purchasedTickets.get(token);
    }

    public Ticket buyTicket(Seat seat) {
        Ticket ticket = availableSeats.get(seat);
        if (ticket == null) {
            if (seat.getRow() < 1 || seat.getRow() > getTotalRows() || seat.getColumn() < 1
                    || seat.getColumn() > getTotalColumns()) {
                throw new SeatNotFoundException("The number of a row or a column is out of bounds!");
            }
            throw new SeatNotAvailableException("The ticket has been already purchased!");
        }
        increaseIncome(ticket);
        purchasedTickets.put(ticket.getToken(), ticket);
        purchasedSeats.add(seat);
        availableSeats.remove(seat);
        return ticket;
    }

    public void returnTicket(Ticket ticket) {
        reduceIncome(ticket);
        purchasedSeats.remove(ticket.getSeat());
        purchasedTickets.remove(ticket);
        availableSeats.put(ticket.getSeat(), ticket);
    }

    public Statistics getStatistics() {
        statistics.setNumberAvailableSeats(getAvailableSeats().size());
        statistics.setNumberPurchasedTickets(getPurchasedSeats().size());
        return statistics;
    }

    private void fillingRoom() {
        for (int i = 1; i <= cinemaRoomConfigProperties.getTotalRows(); i++) {
            for (int j = 1; j <= cinemaRoomConfigProperties.getTotalColumns(); j++) {
                Seat seat = new Seat(i, j);
                Ticket ticket = new Ticket(seat);
                availableSeats.put(seat, ticket);
            }
        }
    }

    private void increaseIncome(Ticket ticket) {
        statistics.setCurrentIncome(statistics.getCurrentIncome() + ticket.getSeat().getPrice());
    }

    private void reduceIncome(Ticket ticket) {
        statistics.setCurrentIncome(statistics.getCurrentIncome() - ticket.getSeat().getPrice());
    }
}
