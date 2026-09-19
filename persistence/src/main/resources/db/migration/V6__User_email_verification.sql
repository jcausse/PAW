ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified_at TIMESTAMP WITH TIME ZONE NULL;

UPDATE users SET email_verified_at = now() WHERE email_verified_at IS NULL;
