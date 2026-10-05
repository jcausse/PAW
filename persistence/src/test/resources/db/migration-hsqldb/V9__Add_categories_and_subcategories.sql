-- HSQLDB version: intentional no-op.
-- In production this migration seeds categories and subcategories
-- (using ON CONFLICT, which HSQLDB does not support). In tests, data is
-- defined by initial-data.sql, so nothing is inserted here.
-- The file is kept so numbering stays aligned with db/migration.
SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS;