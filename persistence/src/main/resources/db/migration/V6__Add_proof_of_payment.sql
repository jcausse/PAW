CREATE TABLE IF NOT EXISTS files (
    file_id SERIAL PRIMARY KEY,
    filename            VARCHAR(255) NOT NULL,
    alt                 VARCHAR(255) NOT NULL,
    content_type        VARCHAR(50),
    data                BYTEA NOT NULL
);

ALTER TABLE offers
    ADD COLUMN IF NOT EXISTS proof_of_payment_id INTEGER REFERENCES files(file_id) ON DELETE SET NULL;