package ar.edu.itba.paw.webapp.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Access to this resource is forbidden");
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
