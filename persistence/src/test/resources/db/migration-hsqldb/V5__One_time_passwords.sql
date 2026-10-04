CREATE TABLE IF NOT EXISTS one_time_passwords (
    requester_id INTEGER NOT NULL PRIMARY KEY REFERENCES users(user_id),
    otp_value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
