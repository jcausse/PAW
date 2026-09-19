#!/usr/bin/env python3
"""
Dumps all href, action, src, and baseUrl attributes across all JSP and custom tag files
grouped by file, for review and verification of URL handling.
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

attr_re = re.compile(r'''(?P<attr>href|action|src)\s*=\s*(?P<q>["'])(?P<val>.*?)(?P=q)''', re.DOTALL)

print("=== URL Attributes Dump ===")
print(f"Scanning directory: {BASE_DIR.relative_to(REPO_ROOT)}\n")

total_attrs = 0
for fpath in files:
    rel = fpath.relative_to(BASE_DIR)
    with open(fpath, "r", encoding="utf-8") as f:
        content = f.read()

    file_matches = []
    for m in attr_re.finditer(content):
        attr = m.group("attr")
        val = m.group("val").strip()
        lineno = content[:m.start()].count("\n") + 1
        file_matches.append((lineno, attr, val))

    if file_matches:
        print(f"File: {rel}")
        for lineno, attr, val in file_matches:
            # truncate very long inline tags if any
            display_val = val if len(val) <= 80 else val[:77] + "..."
            print(f"  L{lineno:<4} {attr:<8} = {display_val}")
        print()
        total_attrs += len(file_matches)

print(f"Total URL attributes found: {total_attrs}\n")
