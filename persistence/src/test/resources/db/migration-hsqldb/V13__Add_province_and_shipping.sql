-- HSQLDB version of V13__Add_province_and_shipping.sql
-- Same as the production migration, but without "ON CONFLICT" (unsupported by HSQLDB).
-- Runs under HSQLDB's PostgreSQL compatibility mode (sql.syntax_pgs=true).

CREATE TABLE IF NOT EXISTS provinces (
    province_id SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE
);

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
    ('tucuman');

ALTER TABLE users ADD COLUMN IF NOT EXISTS province_id INTEGER
    REFERENCES provinces(province_id) ON DELETE SET NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS location_detail VARCHAR(100);

ALTER TABLE listings ADD COLUMN IF NOT EXISTS accepts_shipping BOOLEAN NOT NULL DEFAULT FALSE;
