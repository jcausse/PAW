package ar.edu.itba.paw.webapp.form.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProofOfShippingUploadFormValidator.class)
@Documented
public @interface ValidProofOfShippingUploadForm {
    String message() default "{ValidProofOfShippingUploadForm}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}