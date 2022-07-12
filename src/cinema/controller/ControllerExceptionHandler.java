package cinema.controller;

import cinema.ErrorResponse;
import cinema.exception.PasswordWrongException;
import cinema.exception.SeatNotAvailableException;
import cinema.exception.SeatNotFoundException;
import cinema.exception.TicketNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({SeatNotAvailableException.class, SeatNotFoundException.class, TicketNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleException(RuntimeException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PasswordWrongException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

}
