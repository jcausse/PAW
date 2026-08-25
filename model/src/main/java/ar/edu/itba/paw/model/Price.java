package ar.edu.itba.paw.model;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
@ToString
public final class Price {

    // Amount in indivisible units (e.g. cents)
    // We wrap this in a class to provide helper methods and potentially future support for currencies
    private final @NonNull Long amount;
}
