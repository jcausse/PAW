# Test Route Skill

## Purpose
Learn how to run the dev server, find route URLs, and check routes for errors in the PAW project.

## Prerequisites
- Docker running with `paw-db` container (PostgreSQL on port 5432)
- Maven and Java 21 available

## Running the Dev Server

```bash
# Start database and Jetty server (runs on localhost:8080)
make dev
```

The `make dev` command:
1. Runs `mvn clean`
2. Starts the `paw-db` PostgreSQL container via `.script/db-start.sh`
3. Runs `mvn install -DskipTests -Pdev` to build all modules
4. Starts Jetty via `mvn -pl webapp jetty:run -Pdev` on port 8080

**Note:** The server takes ~15-20 seconds to fully start. Wait for "Started Jetty Server" in logs.

### Alternative: Background Server (for testing/screenshots)

For longer-running sessions (e.g., taking screenshots), run Jetty in background:

```bash
# Start DB first
./.script/db-start.sh

# Build and run Jetty in background
mvn -pl webapp jetty:run -Pdev > /tmp/jetty.log 2>&1 &
sleep 15  # Wait for "Started Jetty Server"
```

Then test with curl and take screenshots. The server runs until you kill it or the environment times out (~58s).

## Finding Route URLs

### Controller Mapping
Routes are defined in `@Controller` classes under `webapp/src/main/java/ar/edu/itba/paw/webapp/controller/`.

Common patterns:
- `@RequestMapping("/listing")` + `@GetMapping("/new/details")` → `GET /listing/new/details`
- `@PostMapping("/new/details")` → `POST /listing/new/details`

### JSP View Resolution
Views are resolved from `WEB-INF/jsp/<view-name>.jsp` where `<view-name>` matches the `ModelAndView` string:
```java
new ModelAndView("listing/new/details")  // → WEB-INF/jsp/listing/new/details.jsp
```

## Checking Routes for Errors

### 1. Quick HTTP Status Check
```bash
curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/listing/new/details?productId=1"
```
Returns: `200` (OK), `400` (Bad Request), `404` (Not Found), `500` (Server Error), `503` (Service Unavailable)

### 2. Full Response Inspection
```bash
curl -s --max-time 10 "http://localhost:8080/listing/new/details?productId=1" | head -100
```
Look for:
- HTML error pages (Jetty 500/404/503 pages)
- JSP compilation errors in response body
- Stack traces in HTML comments

### 3. JSP Compilation Errors
If you see `org.apache.jasper.JasperException` in response:
- Check the line/column mentioned in error
- Common causes:
  - Invalid tag attributes (add to `.tag` file `@attribute` declarations)
  - Missing taglib imports
  - EL expression syntax errors

### 4. Database Dependencies
Many routes require DB data. Seed test data:
```bash
docker exec paw-db psql -U postgres -d paw -c "
INSERT INTO categories (name) VALUES ('Electronics'), ('Vehicles');
INSERT INTO subcategories (name, category_id) VALUES ('Phones', 1), ('Cars', 2);
INSERT INTO products (brand, model, year, subcategory_id) VALUES ('Apple', 'iPhone 15', 2023, 1);
"
```

Then test with `productId=1` (or whatever ID was generated).

### 5. Server Logs
Check `/tmp/dev.log` (or terminal running `make dev`) for:
- Spring startup errors
- Hibernate/SQL errors
- Controller exception stack traces

## Common Issues & Fixes

| Issue | Fix |
|-------|-----|
| `503 Service Unavailable` | DB not ready; wait or restart `paw-db` container |
| `400 Bad Request` | Missing required `@RequestParam` (e.g. `productId`) |
| `404 Not Found` | No data in DB (seed categories/products) or wrong URL |
| `500 JasperException` | Invalid tag attribute in JSP; add to tag file |
| `500 ServletException` | Check server logs for root cause (NPE, constraint violation, etc.) |

## Workflow Summary

1. `make dev` → wait for "Started Jetty Server"
2. Seed DB if needed: `docker exec paw-db psql ...`
3. `curl -s --max-time 10 "http://localhost:8080/<route>" | head -50`
4. If error: inspect response + server logs
5. Fix code → rebuild (Jetty hot-reloads JSPs; Java changes need restart)
6. Re-test

## Important

**When asked to debug an issue, always explain the issue and the fix you found, then ask for confirmation before applying it** unless explicitly told to apply a fix without asking.