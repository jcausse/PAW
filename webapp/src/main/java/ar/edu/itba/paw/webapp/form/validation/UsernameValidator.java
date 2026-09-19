package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.isEmpty()) {
            return true;
        }
        
        int letterCount = 0;
        for (char c : username.toCharArray()) {
            if (!isValidCharacter(c)) {
                return false;
            }
            if (Character.isLetter(c)) {
                letterCount++;
            }
        }
        
        return letterCount >= 3;
    }

    private boolean isValidCharacter(char c) {

        // "If you add @ as a valid character, I will find you, and I will kill you."
        // - Bryan Mills - Taken (2008)
        // Seriously, it would break lots of features, so don't do it.
        final var OTHER_CHARS = Set.of('-', '_');

        return Character.isLetter(c) || Character.isDigit(c) || OTHER_CHARS.contains(c);
    }
}
