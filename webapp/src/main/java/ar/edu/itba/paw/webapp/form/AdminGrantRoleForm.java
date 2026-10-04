package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.Username;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class AdminGrantRoleForm {

    @NotEmpty
    @Size(min = 3, max = 24)
    @Username
    private String username;
}
