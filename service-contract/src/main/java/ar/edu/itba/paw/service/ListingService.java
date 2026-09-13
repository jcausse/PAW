package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.service.dto.ListingFilterDto;

public interface ListingService {
    Listing getById(Long id);

    Listing create(ListingCreationDto dto);

    Page<Listing> search(ListingFilterDto filter);

    Listing purchase(Long id, Long buyerId, String message);
}
