package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.form.PasswordRecoveryRequestForm;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class ValidPasswordRecoveryRequestValidator implements ConstraintValidator<ValidPasswordRecoveryRequest, PasswordRecoveryRequestForm> {

    private final UserService userService;

    @Override
    public boolean isValid(PasswordRecoveryRequestForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        boolean hasEmail = form.getEmail() != null && !form.getEmail().isBlank();
        boolean hasUsername = form.getUsername() != null && !form.getUsername().isBlank();

        if (!hasEmail && !hasUsername) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{NotEmpty.passwordRecoveryRequestForm.identifier}")
                    .addPropertyNode("username")
                    .addConstraintViolation();
            return false;
        }

        if (hasEmail) {
            String email = form.getEmail().trim().toLowerCase();
            if (!userService.isEmailTaken(email)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{error.email.notfound}")
                        .addPropertyNode("email")
                        .addConstraintViolation();
                return false;
            }
        }
        else {
            String username = form.getUsername().trim().toLowerCase();
            if (!userService.isUsernameTaken(username)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{error.username.notfound}")
                        .addPropertyNode("username")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
