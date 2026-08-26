package ar.edu.itba.paw.service.dto;

import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.User;

public record ListingCreationDto(
    String title,
    Price price,
    User creator,
    Product product
) {}
