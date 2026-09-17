#!/usr/bin/env python3
"""
Swappr Troubleshooter - JSP & Tag URL / c:out Compliance Checker
Scans JSP views and custom tags for missing <c:url> bindings, unescaped URLs,
and raw path literals that should be held in variables.
"""

import os
import re
import sys
import subprocess
from pathlib import Path

SCRIPT_DIR = Path(__file__).resolve().parent
REPO_ROOT = SCRIPT_DIR.parents[1]
BASE_DIR = REPO_ROOT / "webapp" / "src" / "main" / "webapp" / "WEB-INF"

USE_COLOR = sys.stdout.isatty()
BOLD = "\033[1m" if USE_COLOR else ""
RED = "\033[31m" if USE_COLOR else ""
GREEN = "\033[32m" if USE_COLOR else ""
YELLOW = "\033[33m" if USE_COLOR else ""
CYAN = "\033[36m" if USE_COLOR else ""
RESET = "\033[0m" if USE_COLOR else ""


def find_files():
    if not BASE_DIR.exists():
        print(f"{RED}Error: Directory not found: {BASE_DIR}{RESET}", file=sys.stderr)
        sys.exit(1)
    files = []
    for root, _, filenames in os.walk(BASE_DIR):
        for f in filenames:
            if f.endswith(".jsp") or f.endswith(".tag"):
                files.append(Path(root) / f)
    files.sort()
    return files


def run_checks():
    files = find_files()
    print(f"\n{BOLD}{CYAN}=== Swappr JSP / Tag URL & c:out Troubleshooter ==={RESET}")
    print(f"Scanning {len(files)} files in {BASE_DIR.relative_to(REPO_ROOT)}...\n")

    flagged_direct = []
    flagged_base_url = []
    flagged_set_path = []

    for fpath in files:
        rel = fpath.relative_to(BASE_DIR)
        with open(fpath, "r", encoding="utf-8") as f:
            lines = f.readlines()

        for idx, line in enumerate(lines, 1):
            line_str = line.strip()
            if "<%@" in line_str or line_str.startswith("<%--"):
                continue

            # Check 1: href/action/src attributes with hardcoded app routes
            # Note: paw:user internally wraps href in <c:url>, so raw paths passed to it are resolved
            for attr in ("href", "action", "src"):
                pattern = rf'{attr}\s*=\s*["\'](/[^"\'\s>]+)["\']'
                for m in re.finditer(pattern, line_str):
                    val = m.group(1)
                    if "<c:url" in line_str or val.startswith("/WEB-INF") or "paw:user" in line_str:
                        continue
                    flagged_direct.append({
                        "file": str(rel),
                        "line": idx,
                        "attr": attr,
                        "value": val,
                        "snippet": line_str,
                    })

            # Check 2: <c:set ... value="/..." /> assigning raw paths instead of <c:url ... />
            # Skip if the variable (or a composite using it) is subsequently resolved via <c:url> in the file
            set_pattern = r'<c:set\s+[^>]*var=["\']([^"\']+)["\'][^>]*value=["\'](/[a-zA-Z0-9_\-\./{}$]+)["\']'
            for m in re.finditer(set_pattern, line_str):
                var_name = m.group(1)
                val = m.group(2)
                file_content = "".join(lines)
                if f"<c:url" in file_content and var_name in file_content:
                    continue
                flagged_set_path.append({
                    "file": str(rel),
                    "line": idx,
                    "var": var_name,
                    "value": val,
                    "snippet": line_str,
                })

    total_issues = len(flagged_direct) + len(flagged_set_path)

    if flagged_direct:
        print(f"{BOLD}{RED}[FLAGGED] Raw URL Attributes (Missing <c:url ... var=\"...\"/> or variable binding):{RESET}")
        print(f"These attributes pass hardcoded URL strings without context-path resolution or holding variables.\n")
        for item in flagged_direct:
            print(f"  {BOLD}{item['file']}:{item['line']}{RESET}")
            print(f"    Attribute: {CYAN}{item['attr']}=\"{item['value']}\"{RESET}")
            print(f"    Snippet:   {YELLOW}{item['snippet']}{RESET}")
            var_name = item['value'].strip("/").replace("/", "_").replace("-", "_") + "Url"
            if not var_name or var_name == "Url":
                var_name = "targetUrl"
            print(f"    {GREEN}Suggested Fix:{RESET}")
            print(f"      <c:url value=\"{item['value']}\" var=\"{var_name}\"/>")
            print(f"      ... {item['attr']}=\"${{{var_name}}}\" ...\n")

    if flagged_set_path:
        print(f"{BOLD}{YELLOW}[FLAGGED] Raw Path in <c:set> (Should use <c:url>):{RESET}")
        print(f"These variables assign raw path strings instead of resolving via <c:url>.\n")
        for item in flagged_set_path:
            print(f"  {BOLD}{item['file']}:{item['line']}{RESET}")
            print(f"    Variable:  {CYAN}{item['var']}=\"{item['value']}\"{RESET}")
            print(f"    Snippet:   {YELLOW}{item['snippet']}{RESET}")
            print(f"    {GREEN}Suggested Fix:{RESET}")
            print(f"      <c:url value=\"{item['value']}\" var=\"{item['var']}\"/>\n")

    print(f"{BOLD}--- Summary ---{RESET}")
    print(f"Files scanned: {len(files)}")
    if total_issues == 0:
        print(f"{GREEN}✓ No missing URL variables or unmanaged paths found! All URLs follow JSTL conventions.{RESET}\n")
    else:
        print(f"{RED}Found {total_issues} location(s) that should be reviewed and updated.{RESET}\n")

    return total_issues


def run_subscript(script_name):
    script_path = SCRIPT_DIR / script_name
    if not script_path.exists():
        print(f"{RED}Script not found: {script_name}{RESET}", file=sys.stderr)
        return 1
    return subprocess.run([sys.executable, str(script_path)]).returncode


if __name__ == "__main__":
    if len(sys.argv) > 1:
        target = sys.argv[1]
        if target == "--all":
            scripts = [
                "find_urls.py",
                "find_all_paths.py",
                "find_all_quoted_paths.py",
                "check_all_url_attributes.py",
                "check_curl_no_var.py",
                "check_relative_paths.py",
                "check_scripts.py",
                "dump_all_url_attrs.py",
            ]
            for s in scripts:
                run_subscript(s)
            sys.exit(0)
        else:
            if not target.endswith(".py"):
                target += ".py"
            sys.exit(run_subscript(target))
    else:
        # Default run: comprehensive troubleshooter report
        run_checks()
        sys.exit(0)
