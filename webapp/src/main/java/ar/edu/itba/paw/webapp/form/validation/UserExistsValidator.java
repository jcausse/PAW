package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.service.UserService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UserExistsValidator implements ConstraintValidator<UserExists, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String usernameOrEmail, ConstraintValidatorContext context) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            return true; // Let @NotEmpty handle empty values
        }
        return userService.getByUsernameOrEmail(usernameOrEmail.trim()).isPresent();
    }
}
