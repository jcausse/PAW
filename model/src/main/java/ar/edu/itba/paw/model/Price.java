package ar.edu.itba.paw.model;

import java.math.BigDecimal;
import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public final class Price {

    private final @NonNull BigDecimal amount;
}
