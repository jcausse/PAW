package ar.edu.itba.paw.service.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public static NotFoundException createFor(String what) {
        return new NotFoundException(what + " not found");
    }
}
