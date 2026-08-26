package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.service.dto.ListingCreationDto;

public interface ListingService {
    Listing getById(Long id);

    Listing create(ListingCreationDto dto);
}
