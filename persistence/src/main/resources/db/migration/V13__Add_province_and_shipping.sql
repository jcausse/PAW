-- Seller location (province + detail) and per-listing shipping availability.

CREATE TABLE IF NOT EXISTS provinces (
    province_id SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE
);

-- The 24 Argentine jurisdictions (23 provinces + CABA) as stable snake_case codes.
INSERT INTO provinces (name) VALUES
    ('buenos_aires'),
    ('caba'),
    ('catamarca'),
    ('chaco'),
    ('chubut'),
    ('cordoba'),
    ('corrientes'),
    ('entre_rios'),
    ('formosa'),
    ('jujuy'),
    ('la_pampa'),
    ('la_rioja'),
    ('mendoza'),
    ('misiones'),
    ('neuquen'),
    ('rio_negro'),
    ('salta'),
    ('san_juan'),
    ('san_luis'),
    ('santa_cruz'),
    ('santa_fe'),
    ('santiago_del_estero'),
    ('tierra_del_fuego'),
    ('tucuman')
ON CONFLICT (name) DO NOTHING;

ALTER TABLE users ADD COLUMN IF NOT EXISTS province_id INTEGER
    REFERENCES provinces(province_id) ON DELETE SET NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS location_detail VARCHAR(100);

-- Keep the invariant: no detail without a province (covers any legacy rows).
UPDATE users SET location_detail = NULL WHERE province_id IS NULL;

ALTER TABLE listings ADD COLUMN IF NOT EXISTS accepts_shipping BOOLEAN NOT NULL DEFAULT FALSE;
