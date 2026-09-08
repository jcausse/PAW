INSERT INTO users (user_id, username, display_name, email, password, image_id)
VALUES (1, 'fake_user', 'Fake User', 'fake@example.com', 'fake_password', NULL);

INSERT INTO categories (category_id, name)
VALUES (1, 'Electronics'), (2, 'Mobile Devices');

INSERT INTO subcategories (subcategory_id, name, category_id)
VALUES (1, 'Laptops', 1), (2, 'Phones', 2);

INSERT INTO products (product_id, brand, model, year, subcategory_id)
VALUES (1, 'Apple', 'MacBook Pro', 2023, 1);