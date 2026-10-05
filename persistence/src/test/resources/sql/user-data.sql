-- Fixtures for UserJdbcDaoTest. User 1 (fake_user) comes from initial-data.sql.

INSERT INTO images (image_id, filename, alt, content_type, data)
VALUES (10, 'avatar.jpg', 'Avatar', 'image/jpeg', X'0A');

-- User 2: verified, has a profile image, located in CABA with detail, with ratings.
INSERT INTO users (user_id, username, display_name, email, password, image_id, email_verified_at,
                   seller_positive_ratings, seller_neutral_ratings, seller_negative_ratings,
                   buyer_positive_ratings, buyer_neutral_ratings, buyer_negative_ratings,
                   province_id, location_detail)
VALUES (2, 'verified_user', 'Verified User', 'verified@example.com', 'secret', 10,
        TIMESTAMP '2026-01-10 09:00:00+00:00',
        3, 1, 0,
        2, 0, 1,
        (SELECT province_id FROM provinces WHERE name = 'caba'), 'Belgrano');

-- User 3: unverified, no image, no province.
INSERT INTO users (user_id, username, display_name, email, password)
VALUES (3, 'plain_user', 'Plain User', 'plain@example.com', 'secret');
