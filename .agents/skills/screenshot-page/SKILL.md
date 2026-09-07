# screenshot-page Skill

## Purpose

Take screenshots of web pages using headless Chromium and send them as attachments via the Discord bot.

## When to Use

- When asked to screenshot a page to check how it looks
- Whenever you finish working on UI changes on a page, take a screenshot of the modified pages and send them
- Ensure each screenshot has a different filename (e.g., include page name, timestamp, or iteration number)

## Workflow

1. **Start the dev server** (if not already running):
   ```bash
   # Option 1: Full dev setup (database + server)
   make dev > /tmp/jetty.log 2>&1 &
   sleep 15  # wait for "Started Jetty Server"
   
   # Option 2: If DB already running, just start Jetty
   mvn -pl webapp jetty:run -Pdev > /tmp/jetty.log 2>&1 &
   sleep 15
   ```

2. **Verify the route works** (combine with `test-route` skill):
   ```bash
   curl -s --max-time 10 "http://localhost:8080/<route>" | head -50
   ```

3. **Take the screenshot** with headless Chromium:
   ```bash
   chromium --headless --window-size=1920,1080 --screenshot="/home/nemo/screenshots/<filename>.png" "http://localhost:8080/<route>"
   ```

4. **Verify the screenshot was created**:
   ```bash
   file /home/nemo/screenshots/<filename>.png
   ```

5. **Send the attachment** by including this JSON at the **very end** of your response inside a **json-tagged code block** (no text after it):
    ```json
    {
      "attachments": [
        {
          "path": "/home/nemo/screenshots/<filename>.png",
          "name": "<filename>.png",
          "type": "image/png",
          "dimensions": "1920x1080",
          "size_bytes": <file_size>
        }
      ]
    }
    ```

## Example

```bash
# Start server
make dev > /tmp/jetty.log 2>&1 &
sleep 10

# Test route first
curl -s --max-time 10 "http://localhost:8080/listing/1" | grep -c "Test Listing"

# Take screenshot
chromium --headless --window-size=1920,1080 --screenshot="/home/nemo/screenshots/listing-1.png" "http://localhost:8080/listing/1"

# Check file
ls -la /home/nemo/screenshots/listing-1.png
```

## Integration with test-route Skill

This skill complements the `test-route` skill:
- Use `test-route` to debug errors and verify routes work
- Once a route renders properly, use `screenshot-page` to capture and share the visual result
- Run the dev server once, then both test the route AND take screenshots in the same session

## Notes

- The dev server must be running on `localhost:8080`
- Screenshots are saved to `/home/nemo/screenshots/` directory
- Use `--window-size=1920,1080` for full HD screenshots
- Chromium may output DBus errors to stderr (normal in headless environments)
- File size can be obtained with `stat -c%s /path/to/file.png` or `ls -la`