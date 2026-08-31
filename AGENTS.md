# PAW Project Rules

## Project Context

This is an ITBA PAW (Proyecto de Aplicaciones Web) university project. It is a multi-module Maven project using Spring WebMVC (not Spring Boot), JSP views, JSTL, and PostgreSQL via Spring JDBC (will later use JPA/Hibernate but not yet until this file changes).

The student has prior Spring/Spring Boot knowledge but must follow the course progression. Features like Spring Security, `@Transactional`, and password hashing will be added later — do not remove commented-out code left as reminders for future implementation.

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

## Other Conventions

- Class names are singular (e.g. `UserService`, `UserJdbcDao`, `User` (model), `Listing`, `Product`).
- Identifier fields (IDs) are spelled `Id` when using `camelCase` (e.g. `productId`).
- Prefer functional-style code and the use of Java Streams.
- Use Optional for return values which may not be present, but DO NOT use Optional as method parameters, as it is a code smell.
- Prefer using `var` for type inference when possible.

## Database Conventions

- PostgreSQL with snake_case column names.
- Table names are plural (e.g., `users`, `listings`).
- `USER` is a reserved keyword in PostgreSQL — always use `users`.
- Existence checks use `SELECT EXISTS(SELECT 1 FROM ...)` returning `Boolean.class`.
- Table creations always use `CREATE TABLE IF NOT EXISTS`.

## Build & Scripts

- Build with `mvn clean compile` to verify changes across all modules.
- Dev server: `make dev` (starts DB container + Jetty).
- Deploy: `make deploy` (runs `.script/deploy.py`) (see associated skill).
- Scripts live in `.script/` directory.
