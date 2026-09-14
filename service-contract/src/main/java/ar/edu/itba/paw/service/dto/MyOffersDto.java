package ar.edu.itba.paw.service.dto;

import ar.edu.itba.paw.model.Offer;
import java.util.List;

public record MyOffersDto(
    List<Offer> pending,
    List<Offer> resolved
) {
}