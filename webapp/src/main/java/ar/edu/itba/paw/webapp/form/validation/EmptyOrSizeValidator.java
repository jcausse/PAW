package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmptyOrSizeValidator implements ConstraintValidator<EmptyOrSize, String> {

    private int min;
    private int max;

    @Override
    public void initialize(EmptyOrSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.length() >= min && value.length() <= max;
    }
}
