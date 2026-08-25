#!/usr/bin/env bash
set -e

DB_CONTAINER="paw-db"
DB_IMAGE="paw-postgres"
DB_PORT="5432"

STATUS=$(docker inspect -f '{{.State.Running}}' "$DB_CONTAINER" 2>/dev/null || true)

if [ "$STATUS" = "true" ]; then
    echo "Database container '$DB_CONTAINER' is already running."
elif [ "$STATUS" = "false" ]; then
    echo "Starting existing database container '$DB_CONTAINER'..."
    docker start "$DB_CONTAINER"
else
    echo "Building database image '$DB_IMAGE'..."
    docker build -t "$DB_IMAGE" .
    echo "Running database container '$DB_CONTAINER' on port $DB_PORT..."
    docker run --name "$DB_CONTAINER" -d -p "$DB_PORT":5432 "$DB_IMAGE"
fi
