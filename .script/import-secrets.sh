#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Switch to repository root
cd "$REPO_ROOT"

FILE="${1:-secrets.tar.gz}"

TMP_DIR=""
if [ -f "$FILE" ]; then
    TMP_DIR=$(mktemp -d)
    trap 'rm -rf "$TMP_DIR"' EXIT
    tar -xzf "$FILE" -C "$TMP_DIR"
    SECRETS_DIR="$TMP_DIR/secrets"
elif [ -d "$REPO_ROOT/secrets" ]; then
    SECRETS_DIR="$REPO_ROOT/secrets"
else
    echo "$FILE not found." >&2
    exit 1
fi

if [ ! -d "$SECRETS_DIR" ]; then
    echo "Error: Directory '$SECRETS_DIR' not found." >&2
    exit 1
fi

echo "Importing secrets from $SECRETS_DIR..."

# 1. Service: mail.properties (dev and prod)
mkdir -p "$REPO_ROOT/service/src/main/resources/env/dev"
mkdir -p "$REPO_ROOT/service/src/main/resources/env/prod"
cp "$SECRETS_DIR/service/dev/mail.properties" "$REPO_ROOT/service/src/main/resources/env/dev/mail.properties"
cp "$SECRETS_DIR/service/prod/mail.properties" "$REPO_ROOT/service/src/main/resources/env/prod/mail.properties"

# 2. Webapp: app.properties (dev and prod)
mkdir -p "$REPO_ROOT/webapp/src/main/resources/env/dev"
mkdir -p "$REPO_ROOT/webapp/src/main/resources/env/prod"
cp "$SECRETS_DIR/webapp/dev/app.properties" "$REPO_ROOT/webapp/src/main/resources/env/dev/app.properties"
cp "$SECRETS_DIR/webapp/prod/app.properties" "$REPO_ROOT/webapp/src/main/resources/env/prod/app.properties"

# 3. Webapp: rememberMe.key
mkdir -p "$REPO_ROOT/webapp/src/main/resources"
cp "$SECRETS_DIR/webapp/rememberMe.key" "$REPO_ROOT/webapp/src/main/resources/rememberMe.key"

# Clean up extracted secrets directory in repo root if present
if [ -d "$REPO_ROOT/secrets" ]; then
    rm -rf "$REPO_ROOT/secrets"
fi

echo "Successfully imported all secrets."
