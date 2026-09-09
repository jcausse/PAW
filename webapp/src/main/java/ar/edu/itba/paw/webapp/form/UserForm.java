package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.Username;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
public class UserForm {

    @NotEmpty
    @Size(min = 3, max = 24)
    @Username
    private String username;

    private String displayName;

    private String email;

    private boolean firstTime;

    private MultipartFile profilePicture;
}
