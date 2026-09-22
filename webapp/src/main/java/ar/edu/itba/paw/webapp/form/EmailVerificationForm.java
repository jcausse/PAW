package ar.edu.itba.paw.webapp.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
@Setter
public class EmailVerificationForm {

    @NotEmpty
    private String usernameOrEmail;

    @NotEmpty
    private String otp;

    public void setEmail(String email) {
        if (this.usernameOrEmail == null || this.usernameOrEmail.isBlank()) {
            this.usernameOrEmail = email;
        }
    }
}
