package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.service.UserService;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email == null || email.isEmpty() || !userService.isEmailTaken(email);
    }
}
