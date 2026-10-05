-- Fixtures for OneTimePasswordJdbcDaoTest. User 1 comes from initial-data.sql.
INSERT INTO users (user_id, username, display_name, email, password)
VALUES (2, 'other_user', 'Other User', 'other@example.com', 'secret');
