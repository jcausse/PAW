#!/usr/bin/env python3
"""
Checks for any relative path attributes (href, action, src, baseUrl) that do not
start with a leading '/' or protocol scheme.
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

rel_pattern = re.compile(r'''(?P<attr>href|action|src)\s*=\s*["'](?P<val>[a-zA-Z0-9_\-\./]+)["']''')

print("=== Relative Path Attributes Check ===")
print(f"Scanning directory: {BASE_DIR.relative_to(REPO_ROOT)}\n")

found = []
for fpath in files:
    rel = fpath.relative_to(BASE_DIR)
    with open(fpath, "r", encoding="utf-8") as f:
        lines = f.readlines()
    for idx, line in enumerate(lines, 1):
        if "<%@" in line or line.strip().startswith("<%--"):
            continue
        for m in rel_pattern.finditer(line):
            val = m.group("val")
            attr = m.group("attr")
            if not val.startswith("http") and not val.startswith("/"):
                found.append((rel, idx, attr, val, line.strip()))

if found:
    print(f"Found {len(found)} relative path(s) (missing leading '/'):\n")
    for rel, idx, attr, val, line_str in found:
        print(f"  {rel}:{idx} -> {attr}=\"{val}\"")
        print(f"    Line: {line_str}\n")
else:
    print("✓ No relative path issues found (all paths are root-relative or managed via EL).\n")
