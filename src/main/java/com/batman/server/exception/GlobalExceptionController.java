package com.batman.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(value = HTTPResourceNotFoundException.class)
    public ResponseEntity<Object> exception(HTTPResourceNotFoundException exception) {
        return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = HTTPBadRequestException.class)
    public ResponseEntity<Object> exception(HTTPBadRequestException exception) {
        return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HTTPResourceConflictException.class)
    public ResponseEntity<Object> exception(HTTPResourceConflictException exception) {
        return new ResponseEntity<>("Conflict", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = HTTPUnauthorizedException.class)
    public ResponseEntity<Object> exception(HTTPUnauthorizedException exception) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = HTTPMethodNotAllowed.class)
    public ResponseEntity<Object> exception(HTTPMethodNotAllowed exception) {
        return new ResponseEntity<>("Method Not Allowed", HttpStatus.METHOD_NOT_ALLOWED);
    }

}
