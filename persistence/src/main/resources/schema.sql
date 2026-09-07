CREATE TABLE IF NOT EXISTS images (
    image_id SERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    alt      VARCHAR(255) NOT NULL,
    content_type VARCHAR(50),
    data     BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    user_id      SERIAL PRIMARY KEY,
    username     VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    email        VARCHAR(254) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    image_id     INTEGER REFERENCES images(image_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS categories (
    category_id  SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS subcategories (
    subcategory_id  SERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    category_id     INTEGER NOT NULL REFERENCES categories(category_id),
    UNIQUE (name, category_id)
);

CREATE TABLE IF NOT EXISTS products (
    product_id      SERIAL PRIMARY KEY,
    brand           VARCHAR(100) NOT NULL,
    model           VARCHAR(100) NOT NULL,
    year            INTEGER NOT NULL,
    subcategory_id  INTEGER REFERENCES subcategories,
    UNIQUE (brand, model, year)
);

CREATE TABLE IF NOT EXISTS listings (
    listing_id    SERIAL PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    description   TEXT,
    creator_id    INTEGER REFERENCES users(user_id),
    product_id    INTEGER REFERENCES products(product_id),
    price         DECIMAL(100, 2) NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    condition     VARCHAR(20) NOT NULL DEFAULT 'GOOD',
    accepts_trade BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS listing_images (
    listing_id    INTEGER NOT NULL REFERENCES listings(listing_id) ON DELETE CASCADE,
    image_id      INTEGER NOT NULL REFERENCES images(image_id) ON DELETE CASCADE,
    display_order INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (listing_id, image_id)
);
