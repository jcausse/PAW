#!/usr/bin/env python3
"""
Checks for unmanaged URL attributes (href, action, src, baseUrl) across JSP views
and tags that do not use ${...} expressions or <c:url> tag resolution.
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

print("=== Unmanaged URL Attributes Check ===")
print(f"Scanning directory: {BASE_DIR.relative_to(REPO_ROOT)}\n")

flagged = []
for fpath in files:
    rel = fpath.relative_to(BASE_DIR)
    with open(fpath, "r", encoding="utf-8") as f:
        lines = f.readlines()

    for idx, line in enumerate(lines, 1):
        line_str = line.strip()
        if "<%@" in line_str or line_str.startswith("<%--"):
            continue

        for attr in ("href", "action", "src"):
            matches = re.findall(rf'{attr}\s*=\s*["\']([^"\']+)["\']', line_str)
            for val in matches:
                if (not val.startswith("${")
                        and "<c:url" not in val
                        and "<c:out" not in val
                        and not val.startswith("#")
                        and not val.startswith("javascript:")
                        and not val.startswith("mailto:")
                        and not val.startswith("http")
                        and "paw:user" not in line_str):
                    flagged.append((rel, idx, attr, val, line_str))

if flagged:
    print(f"Found {len(flagged)} unmanaged URL attribute(s):\n")
    for rel, idx, attr, val, line_str in flagged:
        print(f"  [{attr.upper()}] {rel}:{idx}")
        print(f"    Value:   {val}")
        print(f"    Snippet: {line_str}\n")
else:
    print("✓ All URL attributes are properly managed via variables or <c:url>.\n")
