package de.fhws.fiw.fds.springDemoApp.exception;

public class LinkLocationToPersonNotAllowedException extends RuntimeException {

    public LinkLocationToPersonNotAllowedException() {
        super();
    }

    public LinkLocationToPersonNotAllowedException(String message) {
        super(message);
    }

    public LinkLocationToPersonNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LinkLocationToPersonNotAllowedException(Throwable cause) {
        super(cause);
    }
}
