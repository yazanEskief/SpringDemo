package de.fhws.fiw.fds.springDemoApp.exception;

public class UnsupportedUnlinkOperation extends RuntimeException {

    public UnsupportedUnlinkOperation() {
        super();
    }

    public UnsupportedUnlinkOperation(String message) {
        super(message);
    }

    public UnsupportedUnlinkOperation(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedUnlinkOperation(Throwable cause) {
        super(cause);
    }
}
