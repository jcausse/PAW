#!/usr/bin/env python3
"""
Scans <script> blocks within JSP and tag files for hardcoded application routes.
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

script_re = re.compile(r'<script\b[^>]*>(.*?)</script>', re.DOTALL | re.IGNORECASE)

print("=== Embedded JavaScript Paths Check ===")
print(f"Scanning directory: {BASE_DIR.relative_to(REPO_ROOT)}\n")

found = []
for fpath in files:
    rel = fpath.relative_to(BASE_DIR)
    with open(fpath, "r", encoding="utf-8") as f:
        content = f.read()

    for m in script_re.finditer(content):
        script_text = m.group(1)
        lineno = content[:m.start()].count("\n") + 1
        matches = re.findall(r'''['"](/[^'"]+)['"]''', script_text)
        if matches:
            found.append((rel, lineno, matches))

if found:
    print(f"Found {len(found)} script block(s) with embedded route paths:\n")
    for rel, lineno, matches in found:
        print(f"  {rel}:{lineno}")
        print(f"    Paths: {matches}\n")
else:
    print("✓ No hardcoded application paths found inside <script> blocks.\n")
