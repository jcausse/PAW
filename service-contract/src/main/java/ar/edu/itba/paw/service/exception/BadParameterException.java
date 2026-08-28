package ar.edu.itba.paw.service.exception;

public class BadParameterException extends RuntimeException {

    public BadParameterException(String message) {
        super(message);
    }

    public BadParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadParameterException(Throwable cause) {
        super(cause);
    }

    public static BadParameterException create(String param, String reason) {
        return new BadParameterException(
            "Invalid value for '" + param + "': " + reason
        );
    }
}
