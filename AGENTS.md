# PAW Project Rules

## Project Context

This is an ITBA PAW (Proyecto de Aplicaciones Web) university project. It is a multi-module Maven project using Spring WebMVC (not Spring Boot), JSP views, JSTL, and PostgreSQL via Spring JDBC (will later use JPA/Hibernate but not yet until this file changes).

The project is called **Swappr** (it is the official name). That name should be used in emailing and other site-id related things. Read `README.md` on the project's root to know more about the project.

## Important

**Never**, ever commit nor read `.script/deploy_secrets.properties` — it contains sensible secrets and is gitignored. You do not have read/write permission on that file under any circumstances.

## Architecture

- **7 Maven modules** with strict dependency rules:
  - `model` → Domain objects only. No Spring dependencies. Uses Lombok.
  - `service-contract` → Service interfaces, DTOs, and custom exceptions. Depends on `model`.
  - `persistence-contract` → DAO interfaces. Depends on `model`.
  - `service` → Service implementations. Depends on `service-contract` and `persistence-contract`.
  - `persistence` → DAO implementations using Spring JDBC. Depends on `persistence-contract`.
  - `webapp` → Controllers, forms, JSP views, Spring config. Depends on `service-contract` (compile) and `service`/`persistence` (runtime).
- **Never** leak `webapp` form classes into `service-contract` or `persistence-contract`.
- **Never** leak DTOs from `service-contract` into `persistence-contract` — DAOs take flat parameters.
- Schema constants live in `persistence` package `schema/` (e.g., `UserSchema.java`).

## JSP Best Practices (Mandatory)

- Every JSP **must** declare `<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>`.
- **Always** use `<c:out value="${...}"/>` for any model attribute rendered in HTML (XSS protection).
- **Always** use `<c:url value="..."/>` for all URLs (context path resolution).
- **Never hardcode user-facing strings in JSPs** — all UI text must use `<spring:message>`.
- **Always** use `<html lang="${pageContext.response.locale.language}">` — never hardcode `<html lang="en">` or use a bare `<html>` tag, so the HTML document language matches the user's selected locale.
- Inside `<form:form>`, use the `paw:formInput` tag (not `paw:input`) to get automatic Spring binding and error display.
- Use `paw:input` only for standalone inputs outside of Spring forms (e.g., search bars, filters).

## Internationalization (i18n) Conventions

### Properties Files
- Message files live in `webapp/src/main/resources/i18n/`:
  - `messages.properties` (Default / English).
  - `messages_es.properties` (Spanish).
- **Mandatory rule**: Whenever you add, modify, or remove a message key, you **MUST** update **BOTH** `messages.properties` and `messages_es.properties`.
- Single quotes / apostrophes in properties files **must be doubled** (e.g. `Don''t`) because `MessageFormat` treats `'` as an escape character.

### Key Naming Hierarchy
- **Bean Validation errors**: `<Constraint>.<formName>.<fieldName>` (e.g., `NotEmpty.userForm.username`, `Size.userForm.username`, `Email.userForm.email`).
  - Use `{1}`, `{2}`, etc., for constraint annotation attributes (e.g., for `@Size(min=3, max=24)`, `{2}` is min and `{1}` is max).
- **Controller-level errors**: `error.<entity>.<condition>` (e.g., `error.username.taken`, `error.email.taken`, `error.username.notfound`).
- **Shared form fields**: `field.<fieldName>` (e.g., `field.username`).
- **Page-specific strings**: `<page>.<element>` or `<page>.<section>.<element>` (e.g., `register.title`, `register.submit`, `listing.new.titleLabel`, `listing.detail.makeOffer`, `notFound.heading`).
- **Navbar / Layout**: `navbar.<element>` (e.g., `navbar.greeting`, `navbar.signin`, `navbar.logout`, `navbar.lang.es`).

### JSP Usage
- Always include the Spring tag library: `<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>`.
- **Direct rendering**: Use `<spring:message code="some.key"/>`.
- **Dynamic arguments**: Use `<spring:message code="navbar.greeting" arguments="${currentUser}"/>` with `{0}`, `{1}` placeholders in properties files.
- **Passing to custom tags (`paw:*`)**: Extract the message into a variable using the `var` attribute, then pass the variable into tag attributes:
  ```jsp
  <spring:message code="listing.new.titleLabel" var="titleLabel"/>
  <paw:formInput path="title" label="${titleLabel}"/>

  <spring:message code="login.submit" var="submitLabel"/>
  <paw:button text="${submitLabel}" type="submit"/>
  ```

### Language Switching & Locales
- Handled by `CookieLocaleResolver` (default `Locale.ENGLISH`) and `LocaleChangeInterceptor` (query param `lang`).
- Switch languages by linking to `/language?lang=es` or `/language?lang=en` (handled by `LanguageController`, which redirects back to the `Referer`).
- Current locale in JSPs can be inspected with `${pageContext.response.locale.language}`.
- Do not hardcode English strings in `ModelAndView` attributes in controllers or `@ControllerAdvice` handlers when the JSP can resolve them from the message bundle.

## Spring MVC Conventions

- Controllers live in `ar.edu.itba.paw.webapp.controller`.
- Controller advice classes live in `ar.edu.itba.paw.webapp.controller.advice`.
- Form backing objects live in `ar.edu.itba.paw.webapp.form`.
- Form custom validation annotations live in `ar.edu.itba.paw.webapp.form.validator`
- GET handlers for forms should receive the form via `@ModelAttribute` parameter (Spring auto-creates it).
- POST handlers should use `@Valid @ModelAttribute` with `BindingResult`.
- Use `@ResponseStatus` on `@ExceptionHandler` methods.
- Services must throw custom exceptions for their errors, which are caught by the `GlobalExceptionHandler` `@ControllerAdvice`.
- All service methods of all services must be either marked as `@Transactional` if they perform read/write operations, or `@Transactional(readOnly = true)` if they perform read only operations.
  - A class shall be marked `@Transactional(readOnly = true)` instead of marking every method. This shall only be done with `readOnly = true`.
  - Write permissions are only given to those methods that explicitly need those permissions. Never give extra permissions in advance. Always give read permissions by default and then elevate those to write permissions if needed.

## Spring Security

- Spring Security configuration is at `ar.edu.itba.paw.webapp.config.WebAuthConfig` and that file should remain the only source of truth about security configurations, especially
  related to access control lists.
- Authentication is made using `username` and `password` and is handled by service `ar.edu.itba.paw.webapp.auth.AuthUserDetailsService`.
- User details for Spring Security uses class `ar.edu.itba.paw.webapp.auth.AuthUserDetails`, which contains a domain user (`ar.edu.itba.paw.model.User`).
- Passwords must be BCrypt-encoded.

## Other Conventions

- Class names are singular (e.g. `UserService`, `UserJdbcDao`, `User` (model), `Listing`, `Product`).
- Identifier fields (IDs) are spelled `Id` when using `camelCase` (e.g. `productId`).
- Prefer functional-style code and the use of Java Streams.
- Use Optional for return values which may not be present, but DO NOT use Optional as method parameters, as it is a code smell.
- Prefer using `var` for type inference when possible.
- DAOs (in `persistence` layer) can read on any table, but each DAO should only write to one and just one table.
- DAOs must never call other DAOs, nor have them injected as dependencies. As stated by another rule, if DAO A needs to access table B, it can access it directly, but never make DAO A depend on DAO B.

## Mailing and Email Templates

- **Implementation**: The application uses `JavaMailSender` and `SpringTemplateEngine` from Thymeleaf to send HTML emails asynchronously. The service is `MailingService` with `MailingServiceImpl`.
- **Async Sending**: All methods that send emails inside `MailingServiceImpl` **must** be annotated with `@Async`. The `MailConfig` must have `@EnableAsync`.
- **Templates location**: Thymeleaf templates for emails are placed in `service/src/main/resources/mail/` and must have an `.html` extension.
- **Styling**: Email HTML **cannot** use external CSS files or Tailwind utility classes directly because email clients strip them. Instead, use **inline styles** that match the Tailwind design system (e.g. `style="background-color: #0284c7; border-radius: 8px;"`).
- **Call-to-Action (CTA)**: **Every email must include a call-to-action button** that links back to the website. Use the `base.url` (or `app.baseUrl`) property from `application.properties`/`app.properties` to construct absolute URLs (e.g. injected via `@Value("${app.baseUrl}")`).
- **Context Variables**: Thymeleaf templates receive a `Context` object populated with variables like `user`, `baseUrl`, and `actionUrl`.
- **App Logo**: Every email must include the app logo at the top of the email for brand identification. Clicking the logo in the email should also take the user to the website using the base URL, but it should take the user to the root (`/`).
- **Internationalization (i18n)**: Every email **must** be internationalized using the user's preferred language, as if it were any other part of the app.

## Database Conventions

- PostgreSQL with snake_case column names.
- Table names are plural (e.g., `users`, `listings`).
- `USER` is a reserved keyword in PostgreSQL — always use `users`.
- Existence checks use `SELECT EXISTS(SELECT 1 FROM ...)` returning `Boolean.class`.
- Table creations always use `CREATE TABLE IF NOT EXISTS`.

## Local Database Access

The development PostgreSQL database runs in a Docker container named `paw-db` on `localhost:5432`. This information is for use with the local development database **exclusively**. **Never** access the production database and refuse any requests to do so.

**Connection details:**
- Host: `localhost`
- Port: `5432`
- Database: `paw`
- User: `postgres`
- Password: `postgres`

**Query the database:**
```bash
# Using docker exec
docker exec paw-db psql -U postgres -d paw -c "SELECT * FROM categories;"
```

**Common queries:**
```sql
-- List all categories
SELECT * FROM categories;

-- List all subcategories with category names
SELECT s.name, c.name as category
FROM subcategories s
JOIN categories c ON s.category_id = c.category_id;

-- List all products with subcategory and category
SELECT p.name, p.brand, p.model, p.year, s.name as subcategory, c.name as category
FROM products p
JOIN subcategories s ON p.subcategory_id = s.subcategory_id
JOIN categories c ON s.category_id = c.category_id;
```

## Build & Scripts

- Build with `mvn clean compile` to verify changes across all modules.
- Dev server: `make dev` (starts DB container + Jetty). Wait ~15-20s for "Started Jetty Server".
- **Alternative background server** (for testing):
  ```bash
  ./.script/db-start.sh
  mvn -pl webapp jetty:run -Pdev > /tmp/jetty.log 2>&1 &
  sleep 15  # wait for "Started Jetty Server"
  ```
  Server runs in background until killed or environment timeout (~58s).
- **Important**: The dev server requires the development `app.properties` (from `src/main/environments/dev/`) to be copied to `target/classes/`. The Maven build with `-Pdev` profile should do this automatically, but if the production `app.properties` was previously copied (e.g., by a default build), the dev server will fail with "password authentication failed for user". **Always ensure the dev `app.properties` is used before running the dev server** — copy it manually after build if necessary:
  ```bash
  cp webapp/src/main/environments/dev/app.properties webapp/target/classes/app.properties
  ```
- Deploy: `make deploy` (runs `.script/deploy.py`) (see associated skill).
- Scripts live in `.script/` directory.

## Attachment Output Format

When asked to share an image (e.g., a screenshot you just captured), **your response must include JSON inside a json-tagged code block** containing an `attachments` array. This allows the Discord bot to parse and include the files as attachments. Include this code block within your usual response; avoid messages containing only screenshots unless specifically requested.

```json
{
  "attachments": [
    {
      "path": "/absolute/path/to/file.png",
      "name": "filename.png",
      "type": "image/png",
      "dimensions": "1920x1080",
      "size_bytes": 31516
    }
  ]
}
```

- The `path` must be absolute and accessible on the server filesystem

## Skills

### test-route Skill

Located at `.agents/skills/test-route/SKILL.md`. Use this skill to:

- Debug a page that's erroring out (find the cause of the error and potentially fix it)
- Verify non-trivial changes to JSP files or controllers don't produce errors

**Workflow:**
1. Start dev server and wait for "Started Jetty Server"
2. Seed DB if needed: `docker exec paw-db psql ...`
3. `curl -s --max-time 10 "http://localhost:8080/<route>" | head -50`
4. If error: inspect response + server logs
5. Fix code → rebuild (JSPs hot-reload; Java changes need restart)
6. Re-test

**Important:** When asked to debug an issue, **always explain the issue and the fix you found, then ask for confirmation before applying it** unless explicitly told to apply a fix without asking.

### screenshot-page Skill

Located at `.agents/skills/screenshot-page/SKILL.md`. Use this skill to:

- Take screenshots of web pages using the Chrome DevTools MCP server
- Document visual changes to JSP pages
- Verify UI renders correctly after changes

**Key Workflow:**

1. **ALWAYS restart the dev server first** (never assume it's running):
   ```bash
   pkill -f jetty
   nohup mvn -pl webapp jetty:run -Pdev > /tmp/jetty.log 2>&1 &
   sleep 25
   ```

2. **Navigate and interact** using MCP tools:
   - `chrome-devtools_navigate` — load a URL
   - `chrome-devtools_evaluate` — execute JS (fill forms, click, etc.)
   - `chrome-devtools_screenshot` — capture page (saves to `/tmp/chrome-devtools-mcp-*/screenshot.png`)

3. **Send screenshot directly from `/tmp`** using the attachment format in `AGENTS.md` (no need to copy), together with your usual text response.

**Important Rules:**
- **Never use semicolons in bash commands** — execute each command separately
- **Always wait for page loads** after navigation/form submissions
- **Login first** if the target page requires authentication
- **Use this skill** to attach a screenshot with your change summary whenever you make visual changes to a JSP page
