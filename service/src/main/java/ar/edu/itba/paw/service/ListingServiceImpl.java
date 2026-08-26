package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.persistence.ListingDao;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.service.exception.UserNotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// @Transactional(readOnly = true)
public class ListingServiceImpl implements ListingService {

    private final ListingDao listingDao;

    @Override
    public Listing getById(Long id) {
        return listingDao
            .getById(id)
            .orElseThrow(() -> UserNotFoundException.byId(id));
    }

    @Override
    // @Transactional
    public Listing create(ListingCreationDto dto) {
        Objects.requireNonNull(dto, "ListingCreationDto cannot be null");
        return listingDao.create(
            dto.title(),
            dto.price(),
            dto.creator().getId()
        );
    }
}
