package ar.edu.itba.paw.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class OfferFilter {

    private final Long sellerId;
    private final Long buyerId;
    private final List<OfferStatus> status;
    private final int page;
    private final int pageSize;
}
