-- HSQLDB version of V20__Add_unique_constraint_to_ratings_offer_id.sql (PostgreSQL compatibility mode).
-- Ensure each offer can have at most one rating (1:1 cardinality)
ALTER TABLE ratings
    ADD CONSTRAINT unique_ratings_offer_id UNIQUE (offer_id);
