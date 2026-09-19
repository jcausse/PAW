#!/usr/bin/env python3
"""
Inspects all <c:url> tags across JSP and tag files to distinguish between
variables stored with 'var=' and inline usages printing directly.
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

curl_re = re.compile(r'<c:url\b([^>]*)/?>', re.DOTALL)

print("=== Inline <c:url> Tags (Without var=) Inspector ===")
print(f"Scanning directory: {BASE_DIR.relative_to(REPO_ROOT)}\n")

no_var = []
with_var = 0

for fpath in files:
    rel = fpath.relative_to(BASE_DIR)
    with open(fpath, "r", encoding="utf-8") as f:
        content = f.read()

    for m in curl_re.finditer(content):
        attrs = m.group(1)
        lineno = content[:m.start()].count("\n") + 1
        has_var = "var=" in attrs
        value_match = re.search(r'value\s*=\s*["\']([^"\']*)["\']', attrs)
        val = value_match.group(1) if value_match else "UNKNOWN"
        if not has_var:
            no_var.append((rel, lineno, val, attrs.strip()))
        else:
            with_var += 1

print(f"Stored in variables (<c:url ... var=\"...\">): {with_var}")
print(f"Printed inline (<c:url ...> without var):       {len(no_var)}\n")

if no_var:
    print("Inline <c:url> usages:")
    for rel, lineno, val, tag_text in no_var:
        print(f"  {rel}:{lineno} -> {val}")
        print(f"    <c:url {tag_text}>\n")
