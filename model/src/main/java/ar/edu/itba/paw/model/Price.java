package ar.edu.itba.paw.model;

import java.math.BigDecimal;
import lombok.*;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public final class Price {

    private final @NonNull BigDecimal amount;
}
