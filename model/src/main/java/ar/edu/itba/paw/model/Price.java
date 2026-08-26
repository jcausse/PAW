package ar.edu.itba.paw.model;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public final class Price {

    public Price(Integer integerPart, Integer decimalPart) {
        // TODO check for negative, cents > 100 etc
        this.amount = Long.valueOf(integerPart) * 100 + decimalPart;
    }

    // Amount in indivisible units (e.g. cents)
    // We wrap this in a class to provide helper methods and potentially future support for currencies
    private final @NonNull Long amount;

    public Float asFloat() {
        return Float.valueOf(amount) / 100.0f;
    }
}
