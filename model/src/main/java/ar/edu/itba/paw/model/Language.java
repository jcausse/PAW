package ar.edu.itba.paw.model;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Language {
    SPANISH("es"),
    ENGLISH("en");

    @Getter private final String code;

    public static Language getDefault() {
        return Language.ENGLISH;
    }

    public static Language fromCode(final String code) {
        return Arrays.stream(Language.values())
                .filter(l -> l.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(getDefault());
    }
}
