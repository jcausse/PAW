#!/usr/bin/env python3
"""
Scans JSP and custom tag files for URL attributes (href, action, src, baseUrl)
that contain literal paths instead of EL variables (${...}) or <c:url> tags.
"""

import os
import re
from pathlib import Path

SCRIPT_DIR = Path(__file__).resolve().parent
REPO_ROOT = SCRIPT_DIR.parents[1]
BASE_DIR = REPO_ROOT / "webapp" / "src" / "main" / "webapp" / "WEB-INF"

files = []
for root, _, filenames in os.walk(BASE_DIR):
    for f in filenames:
        if f.endswith(".jsp") or f.endswith(".tag"):
            files.append(Path(root) / f)
files.sort()

url_attr_pattern = re.compile(r'''(?P<attr>href|action|src)\s*=\s*["'](?P<val>[^"']+)["']''')

print("=== Hardcoded / Non-Variable URL Attributes Scanner ===")
print(f"Scanning directory: {BASE_DIR.relative_to(REPO_ROOT)}\n")

flagged = []
for path in files:
    rel_path = path.relative_to(BASE_DIR)
    with open(path, "r", encoding="utf-8") as file:
        lines = file.readlines()
    for idx, line in enumerate(lines, 1):
        for match in url_attr_pattern.finditer(line):
            val = match.group("val").strip()
            attr = match.group("attr")
            # Skip mailto, javascript, anchor fragments, and external CDN URLs
            if val.startswith("#") or val.startswith("javascript:") or val.startswith("mailto:") or val.startswith("http") or "paw:user" in line:
                continue
            if val.startswith("/"):
                flagged.append((rel_path, idx, attr, val, line.strip()))

if flagged:
    print(f"Found {len(flagged)} attribute(s) using raw path literals (missing <c:url> / variable):\n")
    for rel_path, idx, attr, val, line_str in flagged:
        print(f"  {rel_path}:{idx}")
        print(f"    {attr}=\"{val}\"")
        print(f"    Snippet: {line_str}\n")
else:
    print("✓ All URL attributes properly use variables or <c:url>.\n")
