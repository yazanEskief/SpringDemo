package de.fhws.fiw.fds.springDemoApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {PersonNotFoundException.class, LocationNotFoundException.class})
    public ResponseEntity<ExceptionEntity> exceptionHandler(Exception e) {

        ExceptionEntity exceptionEntity = new ExceptionEntity(
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(exceptionEntity, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {LinkLocationToPersonNotAllowedException.class,
            UnsupportedUnlinkOperation.class})
    public ResponseEntity<ExceptionEntity> linkingExceptionHandler(Exception e) {

        ExceptionEntity exceptionEntity = new ExceptionEntity(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(exceptionEntity, HttpStatus.BAD_REQUEST);
    }
}
