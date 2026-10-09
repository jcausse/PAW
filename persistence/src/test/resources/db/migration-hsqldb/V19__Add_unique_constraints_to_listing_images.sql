-- HSQLDB version of V19__Add_unique_constraints_to_listing_images.sql (PostgreSQL compatibility mode).
-- Ensure an image can belong to at most one listing (1:N cardinality)
ALTER TABLE listing_images
    ADD CONSTRAINT unique_listing_images_image_id UNIQUE (image_id);

-- Ensure no two images in the same listing can have the same display order
ALTER TABLE listing_images
    ADD CONSTRAINT unique_listing_images_display_order UNIQUE (listing_id, display_order);
