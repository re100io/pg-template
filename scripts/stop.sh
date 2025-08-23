#!/bin/bash

# PostgreSQL Template Application Stop Script

set -e

echo "Stopping PostgreSQL Template Application..."

# Find the process
PID=$(pgrep -f "pg-template.*\.jar" || true)

if [ -z "$PID" ]; then
    echo "Application is not running"
    exit 0
fi

echo "Found application process: $PID"

# Try graceful shutdown first
echo "Sending TERM signal..."
kill -TERM "$PID"

# Wait for graceful shutdown
for i in {1..30}; do
    if ! kill -0 "$PID" 2>/dev/null; then
        echo "Application stopped gracefully"
        exit 0
    fi
    echo "Waiting for graceful shutdown... ($i/30)"
    sleep 1
done

# Force kill if still running
echo "Graceful shutdown timeout, forcing kill..."
kill -KILL "$PID" 2>/dev/null || true

# Verify it's stopped
sleep 2
if kill -0 "$PID" 2>/dev/null; then
    echo "Error: Failed to stop application"
    exit 1
else
    echo "Application stopped forcefully"
fi