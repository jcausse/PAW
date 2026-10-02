-- V9__Add_categories_and_subcategories.sql
-- Add all categories and subcategories

-- Categories
INSERT INTO categories (name) VALUES
    ('computers'),
    ('photo_video'),
    ('audio'),
    ('gaming'),
    ('tv_entertainment'),
    ('mobile'),
    ('appliances'),
    ('other')
ON CONFLICT (name) DO NOTHING;

-- Subcategories for Computers
INSERT INTO subcategories (name, category_id)
SELECT 'laptops', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'desktop_pcs', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'monitors', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'graphics_cards', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'cpus', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'hard_drives_ssd', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'motherboards', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'memory', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'external_storage', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'computer_peripherals', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'other_pc_parts', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'spare_parts', category_id FROM categories WHERE name = 'computers'
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories for Photo & Video
INSERT INTO subcategories (name, category_id)
SELECT 'cameras', category_id FROM categories WHERE name = 'photo_video'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'camera_lenses', category_id FROM categories WHERE name = 'photo_video'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'cine_cameras', category_id FROM categories WHERE name = 'photo_video'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'action_cameras', category_id FROM categories WHERE name = 'photo_video'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'lighting', category_id FROM categories WHERE name = 'photo_video'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'camera_accessories', category_id FROM categories WHERE name = 'photo_video'
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories for Audio
INSERT INTO subcategories (name, category_id)
SELECT 'headphones', category_id FROM categories WHERE name = 'audio'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'speakers', category_id FROM categories WHERE name = 'audio'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'sound_bars', category_id FROM categories WHERE name = 'audio'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'portable_speakers', category_id FROM categories WHERE name = 'audio'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'microphones', category_id FROM categories WHERE name = 'audio'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'dacs_audio_interfaces', category_id FROM categories WHERE name = 'audio'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'portable_music_players', category_id FROM categories WHERE name = 'audio'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'hifi_equipment', category_id FROM categories WHERE name = 'audio'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'musical_instruments', category_id FROM categories WHERE name = 'audio'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'audio_accessories', category_id FROM categories WHERE name = 'audio'
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories for Gaming
INSERT INTO subcategories (name, category_id)
SELECT 'gaming_consoles', category_id FROM categories WHERE name = 'gaming'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'portable_consoles', category_id FROM categories WHERE name = 'gaming'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'controllers', category_id FROM categories WHERE name = 'gaming'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'vr_headsets', category_id FROM categories WHERE name = 'gaming'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'games', category_id FROM categories WHERE name = 'gaming'
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories for TV & Entertainment
INSERT INTO subcategories (name, category_id)
SELECT 'tvs', category_id FROM categories WHERE name = 'tv_entertainment'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'projectors', category_id FROM categories WHERE name = 'tv_entertainment'
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories for Mobile
INSERT INTO subcategories (name, category_id)
SELECT 'smartphones', category_id FROM categories WHERE name = 'mobile'
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategories (name, category_id)
SELECT 'tablets', category_id FROM categories WHERE name = 'mobile'
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories for Appliances
INSERT INTO subcategories (name, category_id)
SELECT 'home_appliances', category_id FROM categories WHERE name = 'appliances'
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories for Other
INSERT INTO subcategories (name, category_id)
SELECT 'other', category_id FROM categories WHERE name = 'other'
ON CONFLICT (name, category_id) DO NOTHING;