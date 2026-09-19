package ar.edu.itba.paw.model;
import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ListingSort {
    RECENT("recent"),
    PRICE_ASC("price_asc"),
    PRICE_DESC("price_desc"),
    NAME_ASC("name_asc"),
    NAME_DESC("name_desc");

    @Getter private final String key;

    public static Optional<ListingSort> fromString(final String key) {
        return Arrays.stream(ListingSort.values())
            .filter(s -> s.key.equalsIgnoreCase(key))
            .findFirst();
    }
}
