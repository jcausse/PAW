# Troubleshooter

These are just a bunch of AI-generated Python scripts that serve the only purpose of finding missing `<c:out>`s on
JSP and TAG files.

## Usage

Run the full troubleshooter via `make`:
```bash
make troubleshoot
```

Run an individual sub-check or all sub-scripts:
```bash
make troubleshoot ARGS=find_urls
make troubleshoot ARGS=check_curl_no_var
make troubleshoot ARGS=--all
```

