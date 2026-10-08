-- HSQLDB version of V18__Convert_enums_to_uppercase.sql (PostgreSQL compatibility mode).
-- Convert enum values to uppercase across all tables

-- 1. offers: status, buyer_rating, seller_rating
UPDATE offers SET status = UPPER(status);
ALTER TABLE offers ALTER COLUMN status SET DEFAULT 'PENDING';

UPDATE offers SET buyer_rating = UPPER(buyer_rating) WHERE buyer_rating IS NOT NULL;
UPDATE offers SET seller_rating = UPPER(seller_rating) WHERE seller_rating IS NOT NULL;

-- 2. ratings: recreate table with uppercase check constraints
DROP TABLE IF EXISTS ratings;
CREATE TABLE ratings (
    rating_id SERIAL PRIMARY KEY,
    creator_id BIGINT NOT NULL REFERENCES users(user_id),
    rated_id BIGINT NOT NULL REFERENCES users(user_id),
    offer_id BIGINT NOT NULL REFERENCES offers(offer_id),
    role VARCHAR(10) NOT NULL CHECK (role IN ('BUYER', 'SELLER')),
    type VARCHAR(10) NOT NULL CHECK (type IN ('POSITIVE', 'NEUTRAL', 'NEGATIVE')),
    review_text TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_ratings_rated_id ON ratings(rated_id);
CREATE INDEX IF NOT EXISTS idx_ratings_offer_id ON ratings(offer_id);
CREATE INDEX IF NOT EXISTS idx_ratings_creator_id ON ratings(creator_id);

-- 3. users: preferred_language
UPDATE users SET preferred_language = CASE
    WHEN LOWER(preferred_language) = 'es' THEN 'SPANISH'
    WHEN LOWER(preferred_language) = 'en' THEN 'ENGLISH'
    ELSE 'ENGLISH'
END;
ALTER TABLE users ALTER COLUMN preferred_language SET DEFAULT 'ENGLISH';
