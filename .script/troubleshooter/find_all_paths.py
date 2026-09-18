#!/usr/bin/env python3
"""
Scans string literals in JSP and custom tag files for application route paths
(e.g., '/account', '/listing', '/profile') that appear outside of <c:url value="..." /> tags.
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

path_pattern = re.compile(r'''(?P<quote>["'])(?P<path>/[a-zA-Z0-9_\-\.\?&=/{}$]+)(?P=quote)''')

print("=== Application Route Path Literals Scanner ===")
print(f"Scanning directory: {BASE_DIR.relative_to(REPO_ROOT)}\n")

total_paths = 0
unmanaged_paths = []

for path in files:
    rel_path = path.relative_to(BASE_DIR)
    with open(path, "r", encoding="utf-8") as f:
        lines = f.readlines()

    for idx, line in enumerate(lines, 1):
        line_str = line.strip()
        if "<%@" in line_str or line_str.startswith("<%--"):
            continue

        for m in path_pattern.finditer(line_str):
            val = m.group("path")
            if val.startswith("/WEB-INF"):
                continue

            total_paths += 1
            is_c_url = "<c:url" in line_str and (f'value="{val}"' in line_str or f"value='{val}'" in line_str)
            if not is_c_url:
                unmanaged_paths.append((rel_path, idx, val, line_str))

print(f"Total route paths scanned: {total_paths}")
if unmanaged_paths:
    print(f"Found {len(unmanaged_paths)} route path(s) used outside of <c:url value=\"...\">\n")
    for rel_path, idx, val, line_str in unmanaged_paths:
        print(f"  {rel_path}:{idx} -> {val}")
        print(f"    Line: {line_str}\n")
else:
    print("✓ All route paths are defined within <c:url> tags.\n")
