# Test Route Skill

## Purpose
Learn how to run the dev server, find route URLs, and check routes for errors in the PAW project using Chrome DevTools MCP.

## Prerequisites
- Docker running with `paw-db` container (PostgreSQL on port 5432)
- Maven and Java 21 available
- Chrome DevTools MCP server configured in opencode.json (available as `chrome-devtools_*` tools)

## Available Tools
- `chrome-devtools_navigate` - Load a URL in the browser
- `chrome-devtools_evaluate` - Execute JavaScript on the page (e.g., fill forms, click buttons)
- `chrome-devtools_screenshot` - Take a screenshot of the current page (saves to `/tmp/chrome-devtools-mcp-*/screenshot.png`)

## Running the Dev Server

**ALWAYS Restart the Dev Server First. Never rely on the dev server being online** — it wastes a huge amount of time if it's not and the tools error out.

**ALWAYS run the dev server in background!** Running it in foreground will only stall your commands for minutes and waste time; you can't do anything while running the server in foreground.

```bash
# Kill existing Jetty server if running
pkill -f jetty

# Start the dev server in background
nohup mvn -pl webapp jetty:run -Pdev > /tmp/jetty.log 2>&1 &

# Wait for server to start (~25 seconds)
sleep 25

# Verify it's up
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/login
# Should return 200
```

**Important**: Do NOT use commands with semicolons (;) — they will break. Execute each command separately.

**Important**: The dev server requires the development `app.properties` (from `src/main/environments/dev/`) to be copied to `target/classes/`. The Maven build with `-Pdev` profile should do this automatically, but if the production `app.properties` was previously copied (e.g., by a default build), the dev server will fail with "password authentication failed for user". **Always ensure the dev `app.properties` is used before running the dev server** — copy it manually after build if necessary:
```bash
cp webapp/src/main/environments/dev/app.properties webapp/target/classes/app.properties
```

The server runs until you kill it or the environment times out (~58s).

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

## Checking Routes for Errors (Using Chrome DevTools MCP)

### 1. Navigate and Inspect with MCP Tools
```javascript
// Navigate to the route
chrome-devtools_navigate({ url: "http://localhost:8080/listing/new/details?productId=1" })

// Wait for page to load
sleep(2)

// Optionally take a screenshot for visual verification
chrome-devtools_screenshot({})

// Get page content for error inspection
chrome-devtools_evaluate({
  script: "document.documentElement.outerHTML"
})
```

### 2. Quick HTTP Status Check (still valid for API endpoints)
```bash
curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/listing/new/details?productId=1"
```
Returns: `200` (OK), `400` (Bad Request), `404` (Not Found), `500` (Server Error), `503` (Service Unavailable)

### 3. Full Response Inspection via MCP
```javascript
// Get full HTML response
chrome-devtools_evaluate({
  script: "document.documentElement.outerHTML"
})
```
Look for:
- HTML error pages (Jetty 500/404/503 pages)
- JSP compilation errors in response body
- Stack traces in HTML comments
- Console errors (check browser console via evaluate)

### 4. JSP Compilation Errors
If you see `org.apache.jasper.JasperException` in response:
- Check the line/column mentioned in error
- Common causes:
  - Invalid tag attributes (add to `.tag` file `@attribute` declarations)
  - Missing taglib imports
  - EL expression syntax errors

### 5. Database Dependencies
Many routes require DB data. Seed test data:
```bash
docker exec paw-db psql -U postgres -d paw -c "
INSERT INTO categories (name) VALUES ('Electronics'), ('Vehicles');
INSERT INTO subcategories (name, category_id) VALUES ('Phones', 1), ('Cars', 2);
INSERT INTO products (brand, model, year, subcategory_id) VALUES ('Apple', 'iPhone 15', 2023, 1);
"
```

Then test with `productId=1` (or whatever ID was generated).

### 6. Server Logs
Check `/tmp/jetty.log` for:
- Spring startup errors
- Hibernate/SQL errors
- Controller exception stack traces

### 7. Interactive Testing (forms, buttons, etc.)
```javascript
// Fill form and submit
chrome-devtools_evaluate({
  script: `
    document.querySelector('input[name="title"]').value = 'Test Listing';
    document.querySelector('input[name="price"]').value = '100';
    document.querySelector('form').submit();
  `
})

// Wait for redirect
sleep(2)

// Check result
chrome-devtools_evaluate({
  script: "document.documentElement.outerHTML"
})
```

## Common Issues & Fixes

| Issue | Fix |
|-------|-----|
| `503 Service Unavailable` | DB not ready; wait or restart `paw-db` container |
| `400 Bad Request` | Missing required `@RequestParam` (e.g. `productId`) |
| `404 Not Found` | No data in DB (seed categories/products) or wrong URL |
| `500 JasperException` | Invalid tag attribute in JSP; add to tag file |
| `500 ServletException` | Check server logs for root cause (NPE, constraint violation, etc.) |
| MCP navigate error | Dev server not running — restart it |
| Screenshot is blank/white | Page didn't load fully — increase wait time |
| "Unknown argument" for screenshot | Call `screenshot` with empty object `{}` not `{path: "..."}` |

## Workflow Summary

1. **Restart dev server in background** (pkill, nohup mvn, sleep 25, verify)
2. Seed DB if needed: `docker exec paw-db psql ...`
3. **Navigate with `chrome-devtools_navigate`** to the route
4. **Inspect with `chrome-devtools_evaluate`** (get HTML, check console, etc.)
5. Optionally **screenshot with `chrome-devtools_screenshot`** for visual verification
6. If error: inspect response + server logs (`/tmp/jetty.log`)
7. Fix code → rebuild (Jetty hot-reloads JSPs; Java changes need restart)
8. Re-test

## Important

**When asked to debug an issue, always explain the issue and the fix you found, then ask for confirmation before applying it** unless explicitly told to apply a fix without asking.