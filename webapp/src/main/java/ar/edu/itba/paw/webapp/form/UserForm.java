package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.FieldMatch;
import ar.edu.itba.paw.webapp.form.validation.UniqueEmail;
import ar.edu.itba.paw.webapp.form.validation.UniqueUsername;
import ar.edu.itba.paw.webapp.form.validation.Username;
import ar.edu.itba.paw.webapp.form.validation.ValidImages;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
@FieldMatch(first = "password", second = "confirmPassword")
public class UserForm {

    @NotEmpty
    @Size(min = 3, max = 24)
    @Username
    @UniqueUsername
    private String username;

    @NotEmpty
    @Size(max = 50)
    private String displayName;

    @NotEmpty
    @Email
    @Size(max = 254)
    @UniqueEmail
    private String email;

    @NotEmpty
    @Size(min = 8)
    private String password;

    @NotEmpty
    @Size(min = 8)
    private String confirmPassword;

    @ValidImages
    private MultipartFile profilePicture;
}
