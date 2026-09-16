package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidPasswordRecoveryRequestValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPasswordRecoveryRequest {
    String message() default "{ValidPasswordRecoveryRequest.passwordRecoveryRequestForm}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
