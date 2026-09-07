package ar.edu.itba.paw.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    @Getter private final String roleName;

    public static Optional<Role> fromString(final String roleName){
        return Arrays.stream(Role.values()).filter(r -> r.roleName.equalsIgnoreCase(roleName)).findFirst();
    }
}
