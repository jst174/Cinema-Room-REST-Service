package cinema.controller;

import cinema.dto.CinemaRoom;
import cinema.dto.Statistics;
import cinema.exception.PasswordWrongException;
import cinema.model.Seat;
import cinema.model.Ticket;
import cinema.service.CinemaRoomService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CinemaRoomController {

    private final CinemaRoomService cinemaRoomService;

    public CinemaRoomController(CinemaRoomService cinemaRoomService) {
        this.cinemaRoomService = cinemaRoomService;
    }

    @GetMapping("/seats")
    public ResponseEntity<CinemaRoom> getRoom() {
        List<Seat> availableSeats = cinemaRoomService.getAvailableSeats();
        CinemaRoom cinemaRoom = new CinemaRoom(
                cinemaRoomService.getTotalRows(),
                cinemaRoomService.getTotalColumns(),
                availableSeats);
        return new ResponseEntity<>(cinemaRoom, HttpStatus.OK);
    }

    @PostMapping("/purchase")
    public ResponseEntity<Ticket> purchaseTicket(@RequestBody Seat seat) {
        Ticket ticket = cinemaRoomService.buyTicket(seat);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnTicket(@RequestBody Map<String, String> json) {
        String token = json.get("token");
        Ticket ticket = cinemaRoomService.findTicketByToken(token);
        cinemaRoomService.returnTicket(ticket);
        return new ResponseEntity<>(Map.of("returned_ticket", ticket.getSeat()), HttpStatus.OK);
    }

    @PostMapping("/stats")
    public ResponseEntity<Statistics> getStats(@RequestParam(required = false) String password) {
        if (password == null || !password.equals("super_secret")) {
            throw new PasswordWrongException("The password is wrong!");
        } else {
            Statistics statistics = cinemaRoomService.getStatistics();
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        }
    }
}
