#!/usr/bin/env python3
"""
Inspects all quoted string literals starting with '/' in JSP and custom tag files,
identifying any paths that are not directly defined within <c:url> or <c:param>.
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

pattern = re.compile(r'''["'](/[^"'\s>]+)["']''')

print("=== Quoted Path Literals Inspector ===")
print(f"Scanning directory: {BASE_DIR.relative_to(REPO_ROOT)}\n")

found = []
for fpath in files:
    rel = fpath.relative_to(BASE_DIR)
    with open(fpath, "r", encoding="utf-8") as f:
        lines = f.readlines()
    for idx, line in enumerate(lines, 1):
        if "<%@" in line or line.strip().startswith("<%--"):
            continue
        for m in pattern.finditer(line):
            val = m.group(1)
            if val.startswith("/WEB-INF"):
                continue
            found.append((rel, idx, val, line.strip()))

not_c_url = []
for rel, idx, val, line_str in found:
    if ("<c:url" in line_str or "<c:param" in line_str) and (f'value="{val}"' in line_str or f"value='{val}'" in line_str):
        continue
    not_c_url.append((rel, idx, val, line_str))

print(f"Total quoted paths found: {len(found)}")
if not_c_url:
    print(f"Found {len(not_c_url)} path(s) not directly declared in <c:url value=...>:\n")
    for rel, idx, val, line_str in not_c_url:
        print(f"  {rel}:{idx} -> {val}")
        print(f"    Line: {line_str}\n")
else:
    print("✓ All quoted paths are inside <c:url> or <c:param> declarations.\n")
