package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidImagesValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImages {
    String message() default "{ValidImages.detailsForm.images}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    long maxSizeBytes() default 5 * 1024 * 1024; // 5 MB per file
}
