#!/usr/bin/env bash
set -e

DB_CONTAINER="paw-db"

if [ "$(docker inspect -f '{{.State.Running}}' "$DB_CONTAINER" 2>/dev/null || true)" = "true" ]; then
    echo "Stopping database container '$DB_CONTAINER'..."
    docker stop "$DB_CONTAINER"
else
    echo "Database container '$DB_CONTAINER' is not running."
fi
