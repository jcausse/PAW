package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.EmptyOrSize;
import ar.edu.itba.paw.webapp.form.validation.FieldMatch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
@FieldMatch(first = "password", second = "confirmPassword")
public class UserEditForm {

    @Size(max = 50)
    private String displayName;

    @Email
    @Size(max = 254)
    private String email;

    @EmptyOrSize(min = 8)
    private String password;

    @EmptyOrSize(min = 8)
    private String confirmPassword;

    private MultipartFile profilePicture;
}
