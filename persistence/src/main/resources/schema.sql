CREATE TABLE IF NOT EXISTS users (
    user_id      SERIAL PRIMARY KEY,
    username     VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    email        VARCHAR(254) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
  product_id    SERIAL PRIMARY KEY,
  name          VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS listings (
  listing_id    SERIAL PRIMARY KEY,
  title         VARCHAR(255) NOT NULL,
  creator_id    INTEGER REFERENCES users,
  product_id    INTEGER REFERENCES products,
  price         DECIMAL(100, 2) NOT NULL
);
