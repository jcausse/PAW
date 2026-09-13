package ar.edu.itba.paw.model;

import lombok.*;

import java.time.Instant;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
public final class OneTimePassword {

    private final @NonNull Long requesterId;
    private final @NonNull String otpValue;
    private final @NonNull Instant createdAt;
}
