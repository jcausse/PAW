package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.service.dto.ListingFilterDto;
import java.util.List;

public interface ListingService {
    Listing getById(Long id);

    Listing create(ListingCreationDto dto);

    List<Listing> search(ListingFilterDto filter);
}
