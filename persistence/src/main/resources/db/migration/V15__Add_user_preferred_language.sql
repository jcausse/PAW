-- Preferred language per user, used to localize emails sent to that user
-- (independently of the acting user's request locale). Defaults to English
-- so existing users keep working.
ALTER TABLE users ADD COLUMN IF NOT EXISTS preferred_language VARCHAR(8) NOT NULL DEFAULT 'en';
