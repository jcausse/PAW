package ar.edu.itba.paw.webapp.exception;

public class UserNotAuthenticatedException extends RuntimeException {
    
    public UserNotAuthenticatedException() {
        super("User is not authenticated");
    }

    public UserNotAuthenticatedException(String message) {
        super(message);
    }

    public UserNotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
