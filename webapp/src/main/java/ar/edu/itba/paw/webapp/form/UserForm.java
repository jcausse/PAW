package ar.edu.itba.paw.webapp.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserForm {

    private String username;
    private String firstName;
    private String lastName;
    private String email;

    //    private String password;              // DO NOT DELETE
    //    private String confirmPassword;       // DO NOT DELETE
}
