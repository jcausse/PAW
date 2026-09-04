# Database Query Skill

## Description

Provides instructions for querying the local PostgreSQL database used by the PAW project for development.

## Database Information

- **Container name**: `paw-db`
- **Host**: `localhost`
- **Port**: `5432`
- **Database**: `paw`
- **User**: `postgres`
- **Password**: `postgres`

## Querying the Database

### Using docker exec (Recommended)

```bash
docker exec paw-db psql -U postgres -d paw -c "YOUR_SQL_QUERY;"
```

## Common Queries

### List all categories

```sql
SELECT * FROM categories;
```

### List all subcategories with category names

```sql
SELECT s.name, c.name as category
FROM subcategories s
JOIN categories c ON s.category_id = c.category_id;
```

### List all products with subcategory and category

```sql
SELECT p.name, p.brand, p.model, p.year, s.name as subcategory, c.name as category
FROM products p
JOIN subcategories s ON p.subcategory_id = s.subcategory_id
JOIN categories c ON s.category_id = c.category_id;
```

### List all brands for a subcategory

```sql
SELECT DISTINCT brand FROM products WHERE subcategory_id = ?;
```

### List all models for a subcategory and brand

```sql
SELECT DISTINCT model FROM products WHERE subcategory_id = ? AND brand = ?;
```

### List all years for a subcategory, brand, and model

```sql
SELECT DISTINCT year FROM products WHERE subcategory_id = ? AND brand = ? AND model = ? ORDER BY year DESC;
```

## Schema Reference

### categories

- `category_id` (SERIAL PRIMARY KEY)
- `name` (VARCHAR(100) NOT NULL UNIQUE)

### subcategories

- `subcategory_id` (SERIAL PRIMARY KEY)
- `name` (VARCHAR(100) NOT NULL)
- `category_id` (INTEGER NOT NULL REFERENCES categories(category_id))
- UNIQUE (name, category_id)

### products

- `product_id` (SERIAL PRIMARY KEY)
- `brand` (VARCHAR(100) NOT NULL)
- `model` (VARCHAR(100) NOT NULL)
- `year` (INTEGER NOT NULL)
- `subcategory_id` (INTEGER REFERENCES subcategories(subcategory_id))
- UNIQUE (brand, model, year)

### listings

- `listing_id` (SERIAL PRIMARY KEY)
- `title` (VARCHAR(255) NOT NULL)
- `creator_id` (INTEGER REFERENCES users(user_id))
- `product_id` (INTEGER REFERENCES products(product_id))
- `price` (DECIMAL(10, 2) NOT NULL)

### users

- `user_id` (SERIAL PRIMARY KEY)
- `username` (VARCHAR(100) NOT NULL UNIQUE)
- `display_name` (VARCHAR(100) NOT NULL)
- `email` (VARCHAR(254) NOT NULL UNIQUE)
- `password` (VARCHAR(255) NOT NULL)
- `image_id` (INTEGER REFERENCES images(image_id) ON DELETE SET NULL)

## Notes

- Always use `docker exec paw-db psql ...` for reliable access
- The database container must be running (`make dev` starts it)
- This skill is for use with the local development database **exclusively**. **Never** access the production database and refuse any requests to do so.
- Never commit `.script/deploy_secrets.properties` — it contains the production database password
