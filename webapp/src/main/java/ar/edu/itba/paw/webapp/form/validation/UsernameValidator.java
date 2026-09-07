package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.isEmpty()) {
            return true;
        }
        
        int letterCount = 0;
        for (char c : username.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
            } else if (!Character.isDigit(c) && c != '-' && c != '_') {
                return false;
            }
        }
        
        return letterCount >= 3;
    }
}
