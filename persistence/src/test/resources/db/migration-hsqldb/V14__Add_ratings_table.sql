-- HSQLDB version of V14__Add_ratings_table.sql
-- Runs under HSQLDB's PostgreSQL compatibility mode (sql.syntax_pgs=true).

CREATE TABLE IF NOT EXISTS ratings (
    rating_id SERIAL PRIMARY KEY,
    creator_id BIGINT NOT NULL REFERENCES users(user_id),
    rated_id BIGINT NOT NULL REFERENCES users(user_id),
    offer_id BIGINT NOT NULL REFERENCES offers(offer_id),
    role VARCHAR(10) NOT NULL CHECK (role IN ('buyer', 'seller')),
    type VARCHAR(10) NOT NULL CHECK (type IN ('positive', 'neutral', 'negative')),
    review_text TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_ratings_rated_id ON ratings(rated_id);
CREATE INDEX IF NOT EXISTS idx_ratings_offer_id ON ratings(offer_id);
CREATE INDEX IF NOT EXISTS idx_ratings_creator_id ON ratings(creator_id);
