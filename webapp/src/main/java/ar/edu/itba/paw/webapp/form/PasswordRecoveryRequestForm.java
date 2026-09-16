package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.ValidPasswordRecoveryRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@ValidPasswordRecoveryRequest
public class PasswordRecoveryRequestForm {

    private String username;
    private String email;
}
