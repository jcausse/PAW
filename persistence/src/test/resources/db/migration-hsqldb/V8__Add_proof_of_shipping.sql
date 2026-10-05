-- HSQLDB version: HSQLDB does not support multiple actions in a single ALTER TABLE,
-- so each column is added in its own statement.
ALTER TABLE offers ADD COLUMN IF NOT EXISTS proof_of_shipping_id INTEGER REFERENCES files(file_id) ON DELETE SET NULL;
ALTER TABLE offers ADD COLUMN IF NOT EXISTS tracking_number VARCHAR(255);