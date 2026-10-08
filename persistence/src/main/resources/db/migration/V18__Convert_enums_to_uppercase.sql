-- Convert enum values to uppercase across all tables

-- 1. offers: status, buyer_rating, seller_rating
UPDATE offers SET status = UPPER(status);
ALTER TABLE offers ALTER COLUMN status SET DEFAULT 'PENDING';

UPDATE offers SET buyer_rating = UPPER(buyer_rating) WHERE buyer_rating IS NOT NULL;
UPDATE offers SET seller_rating = UPPER(seller_rating) WHERE seller_rating IS NOT NULL;

-- 2. ratings: role, type
ALTER TABLE ratings DROP CONSTRAINT IF EXISTS ratings_role_check;
UPDATE ratings SET role = UPPER(role);

ALTER TABLE ratings DROP CONSTRAINT IF EXISTS ratings_type_check;
UPDATE ratings SET type = UPPER(type);

-- 3. users: preferred_language
UPDATE users SET preferred_language = CASE
    WHEN LOWER(preferred_language) = 'es' THEN 'SPANISH'
    WHEN LOWER(preferred_language) = 'en' THEN 'ENGLISH'
    ELSE 'ENGLISH'
END;
ALTER TABLE users ALTER COLUMN preferred_language SET DEFAULT 'ENGLISH';
