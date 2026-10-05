INSERT INTO files (file_id, filename, alt, content_type, data)
VALUES (1, 'payment.pdf', 'Proof of payment', 'application/pdf', X'010203'),
       (2, 'shipping.jpg', 'Proof of shipping', 'image/jpeg', X'0102');

INSERT INTO offers (offer_id, listing_id, buyer_id, amount, is_full_price, status, message, created_at,
                    proof_of_payment_id, proof_of_shipping_id, tracking_number, accepted_at,
                    buyer_rating, seller_rating, offered_listing_id)
VALUES (5, 3, 1, 300.00, TRUE, 'accepted', 'Is it still available?', TIMESTAMP '2026-01-03 10:00:00+00:00',
        1, 2, 'TRACK-123', TIMESTAMP '2026-01-04 10:00:00+00:00', 'positive', NULL, NULL),
       (6, 4, 1, 500.00, FALSE, 'pending_payment', 'Trade plus cash', TIMESTAMP '2026-01-07 10:00:00+00:00',
        NULL, NULL, NULL, NULL, NULL, NULL, 1),
       (7, 3, 1, 300.00, TRUE, 'accepted', NULL, TIMESTAMP '2026-01-08 10:00:00+00:00',
        NULL, NULL, NULL, TIMESTAMP '2026-01-10 10:00:00+00:00', 'positive', 'neutral', NULL),
       (8, 3, 1, 300.00, TRUE, 'accepted', NULL, TIMESTAMP '2026-01-09 10:00:00+00:00',
        NULL, NULL, NULL, TIMESTAMP '2026-03-01 10:00:00+00:00', NULL, NULL, NULL);