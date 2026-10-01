ALTER TABLE offers
    ADD COLUMN IF NOT EXISTS offered_listing_id INTEGER REFERENCES listings(listing_id) ON DELETE SET NULL;