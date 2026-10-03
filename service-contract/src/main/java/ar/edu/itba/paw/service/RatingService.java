package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferRating;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.model.RatingFilter;
import ar.edu.itba.paw.model.RatingRole;
import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface RatingService {

    Optional<Rating> getById(Long id);

    Page<Rating> get(RatingFilter filter);

    Rating create(User creator, User rated, Offer offer, RatingRole role, OfferRating type, String reviewText);
}