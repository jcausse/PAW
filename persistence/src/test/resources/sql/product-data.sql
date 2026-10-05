-- Extra products for ProductJdbcDaoTest. Product 1 (Apple MacBook Pro 2023, subcat 1)
-- comes from initial-data.sql. All of these are in subcategory 1 (Laptops) unless noted.

INSERT INTO products (product_id, brand, model, year, subcategory_id) VALUES
    -- Same brand (Apple) and model (MacBook Pro), different year -> for DISTINCT/ORDER on years
    (2, 'Apple', 'MacBook Pro', 2021, 1),
    (3, 'Apple', 'MacBook Pro', 2024, 1),
    -- Same brand (Apple), different model -> for DISTINCT/ORDER on models
    (4, 'Apple', 'MacBook Air', 2022, 1),
    -- Other brands in the same subcategory -> for DISTINCT/ORDER on brands
    (5, 'Dell', 'XPS 15', 2023, 1),
    (6, 'Lenovo', 'ThinkPad X1', 2023, 1),
    -- Duplicate brand (Dell) to ensure brands are de-duplicated
    (7, 'Dell', 'Inspiron 14', 2022, 1),
    -- A product in a different subcategory (Phones, subcat 2 / category 2) -> must not leak
    (8, 'Samsung', 'Galaxy S23', 2023, 2);
