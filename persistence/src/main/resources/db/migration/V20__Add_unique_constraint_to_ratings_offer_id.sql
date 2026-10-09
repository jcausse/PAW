-- Ensure each offer can have at most one rating (1:1 cardinality)
ALTER TABLE ratings ADD CONSTRAINT unique_ratings_offer_id UNIQUE (offer_id);
