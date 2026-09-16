package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.FieldMatch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
@FieldMatch(first = "password", second = "repeatPassword")
public class PasswordRecoveryVerificationForm {

    private String email;
    private String username;

    @NotEmpty
    private String otp;

    @NotEmpty
    @Size(min = 8)
    private String password;

    @NotEmpty
    @Size(min = 8)
    private String repeatPassword;
}
