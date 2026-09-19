package ar.edu.itba.paw.webapp.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds the currently authenticated {@link ar.edu.itba.paw.model.User} to a controller method parameter.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {

    /**
     * Whether authentication is required. Defaults to {@code true}.
     * If {@code true} and no user is authenticated, a
     * {@link ar.edu.itba.paw.webapp.exception.UserNotAuthenticatedException} is thrown.
     * If {@code false} and no user is authenticated, {@code null} will be resolved.
     */
    boolean required() default true;
}
