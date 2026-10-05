-- HSQLDB version of V15__Add_user_preferred_language.sql (PostgreSQL compatibility mode).
ALTER TABLE users ADD COLUMN IF NOT EXISTS preferred_language VARCHAR(8) NOT NULL DEFAULT 'en';
