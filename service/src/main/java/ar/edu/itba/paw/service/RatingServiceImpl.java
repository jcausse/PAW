package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OfferRating;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.model.RatingFilter;
import ar.edu.itba.paw.model.RatingRole;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.RatingDao;
import ar.edu.itba.paw.service.exception.BadParameterException;
import ar.edu.itba.paw.service.exception.NotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RatingServiceImpl implements RatingService {

    private final RatingDao ratingDao;
    private final UserService userService;
    private final OfferService offerService;

    @Autowired
    public RatingServiceImpl(RatingDao ratingDao, UserService userService, OfferService offerService) {
        this.ratingDao = ratingDao;
        this.userService = userService;
        this.offerService = offerService;
    }

    @Override
    public Optional<Rating> getById(Long id) {
        return ratingDao.getById(id);
    }

    @Override
    public Page<Rating> get(RatingFilter filter) {
        Objects.requireNonNull(filter, "RatingFilter cannot be null");
        return ratingDao.search(filter);
    }

    @Override
    @Transactional
    public Rating create(Long creatorId, Long ratedId, Long offerId, RatingRole role, OfferRating type, String reviewText) {
        Objects.requireNonNull(creatorId, "creatorId cannot be null");
        Objects.requireNonNull(ratedId, "ratedId cannot be null");
        Objects.requireNonNull(offerId, "offerId cannot be null");
        Objects.requireNonNull(role, "role cannot be null");
        Objects.requireNonNull(type, "type cannot be null");

        if (!creatorId.equals(ratedId)) {
            var creator = userService.getById(creatorId)
                    .orElseThrow(() -> NotFoundException.createFor("Creator user"));
            var rated = userService.getById(ratedId)
                    .orElseThrow(() -> NotFoundException.createFor("Rated user"));

            var offer = offerService.getById(offerId)
                    .orElseThrow(() -> NotFoundException.createFor("Offer"));

            if (role == RatingRole.BUYER) {
                if (!offer.getBuyer().getId().equals(ratedId)) {
                    throw new BadParameterException("Rated user must be the buyer of the offer");
                }
                if (!offer.getListing().getCreator().getId().equals(creatorId)) {
                    throw new BadParameterException("Creator user must be the seller of the offer");
                }
            } else {
                if (!offer.getListing().getCreator().getId().equals(ratedId)) {
                    throw new BadParameterException("Rated user must be the seller of the offer");
                }
                if (!offer.getBuyer().getId().equals(creatorId)) {
                    throw new BadParameterException("Creator user must be the buyer of the offer");
                }
            }

            if (offer.getStatus() != ar.edu.itba.paw.model.OfferStatus.ACCEPTED) {
                throw new BadParameterException("Only accepted offers can be rated");
            }
        }

        return ratingDao.create(creatorId, ratedId, offerId, role, type, reviewText, Instant.now());
    }
}