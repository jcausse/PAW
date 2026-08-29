package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserForm {

    @NotEmpty
    @Size(min = 3, max = 24)
    private String username;

    @NotEmpty
    @Size(max = 50)
    private String displayName;

    @NotEmpty
    @Email
    @Size(max = 254)
    private String email;

    @NotEmpty
    @Size(min = 8)
    private String password;

    @NotEmpty
    @Size(min = 8)
    private String confirmPassword;
}
