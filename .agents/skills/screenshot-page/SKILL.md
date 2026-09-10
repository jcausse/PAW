# Screenshot Page Skill

## Purpose
Learn how to take screenshots of web pages using the Chrome DevTools MCP server in the PAW project.

## Prerequisites
- Chrome DevTools MCP server configured in opencode.json (available as `chrome-devtools_*` tools)
- Maven and Java 21 available
- Docker running with `paw-db` container (PostgreSQL on port 5432)

## Available Tools
- `chrome-devtools_navigate` - Load a URL in the browser
- `chrome-devtools_evaluate` - Execute JavaScript on the page (e.g., fill forms, click buttons)
- `chrome-devtools_screenshot` - Take a screenshot of the current page (saves to `/tmp/chrome-devtools-mcp-*/screenshot.png`)

## Workflow

### 1. ALWAYS Restart the Dev Server First
**Never rely on the dev server being online** — it wastes a huge amount of time if it's not and the tools error out.

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

### 2. Navigate to the Target Page
```javascript
// Navigate to login page
chrome-devtools_navigate({ url: "http://localhost:8080/login" })

// Fill login form and submit
chrome-devtools_evaluate({
  script: `
    document.querySelector('input[name="username"]').value = 'seller1';
    document.querySelector('input[name="password"]').value = 'sellerpass';
    document.querySelector('form').submit();
  `
})

// Wait for redirect
sleep(3)

// Navigate to target page
chrome-devtools_navigate({ url: "http://localhost:8080/account/listings" })
```

### 3. Take Screenshot
```javascript
// Take screenshot - saved to /tmp/chrome-devtools-mcp-*/screenshot.png
chrome-devtools_screenshot({})
```

The screenshot is automatically saved to a temp directory like `/tmp/chrome-devtools-mcp-XXXXXX/screenshot.png`. **No need to copy it** — you can send it directly from there using the attachment format.

### 4. Send as Attachment
Use the attachment format defined in `AGENTS.md`:

```json
{
  "attachments": [
    {
      "path": "/tmp/chrome-devtools-mcp-XXXXXX/screenshot.png",
      "name": "descriptive-name.png",
      "type": "image/png",
      "dimensions": "1905x2053",
      "size_bytes": 127161
    }
  ]
}
```

Get dimensions and size with:
```bash
file /tmp/chrome-devtools-mcp-*/screenshot.png
stat -c%s /tmp/chrome-devtools-mcp-*/screenshot.png
```

## Important Considerations

1. **ALWAYS restart the dev server** before taking screenshots — don't assume it's running
2. **Execute commands separately** — no semicolons in bash commands
3. **Use `/tmp` path directly** — no need to copy screenshots to a permanent location
4. **Wait for page loads** — add appropriate delays after navigation and form submissions
5. **Login first** if the target page requires authentication

## When to Use
- After making visual changes to JSP files
- When asked to verify a page renders correctly
- For documenting UI changes in PRs or discussions
- When debugging layout/CSS issues

## Common Issues

| Issue | Fix |
|-------|-----|
| `MCP tool returned an error` on navigate | Dev server not running — restart it |
| Screenshot is blank/white | Page didn't load fully — increase wait time |
| Login fails | Check credentials; verify form field names |
| "Unknown argument" for screenshot | Call `screenshot` with empty object `{}` not `{path: "..."}` |