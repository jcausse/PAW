package ar.edu.itba.paw.service.dto;

import ar.edu.itba.paw.model.Offer;
import java.util.List;

public record IncomingOffersDto(
    List<Offer> pending,
    List<Offer> resolved
) {
}