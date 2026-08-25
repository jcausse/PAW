package ar.edu.itba.paw.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class User {
    private final Long id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
}
