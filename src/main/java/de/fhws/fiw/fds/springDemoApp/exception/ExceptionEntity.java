package de.fhws.fiw.fds.springDemoApp.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ExceptionEntity {

    private String message;
    private HttpStatus httpStatus;

    private LocalDateTime timestamp;

    public ExceptionEntity() {
    }

    public ExceptionEntity(String message, HttpStatus httpStatus, LocalDateTime timestamp) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
