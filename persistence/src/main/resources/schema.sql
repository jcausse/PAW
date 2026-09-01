CREATE TABLE IF NOT EXISTS images (
    image_id SERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    alt      VARCHAR(255) NOT NULL,
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
    name            VARCHAR(255) NOT NULL,
    brand           VARCHAR(100),
    model           VARCHAR(100),
    year            INTEGER,
    subcategory_id  INTEGER REFERENCES subcategories,
    UNIQUE (brand, model, year),
    UNIQUE (name, year)
);

CREATE TABLE IF NOT EXISTS listings (
    listing_id    SERIAL PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    creator_id    INTEGER REFERENCES users(user_id),
    product_id    INTEGER REFERENCES products(product_id),
    price         DECIMAL(100, 2) NOT NULL
);
