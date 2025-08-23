#!/bin/bash

# PostgreSQL Template Application Startup Script
# Usage: ./start.sh [profile] [port]

set -e

# Default values
DEFAULT_PROFILE="prod"
DEFAULT_PORT="7001"
DEFAULT_MEMORY="512m"

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="$(dirname "$SCRIPT_DIR")"

# Configuration
PROFILE="${1:-$DEFAULT_PROFILE}"
PORT="${2:-$DEFAULT_PORT}"
MEMORY="${JAVA_MEMORY:-$DEFAULT_MEMORY}"

# Find JAR file
JAR_FILE=$(find "$APP_DIR/lib" -name "*.jar" -not -name "*-sources.jar" | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "Error: No JAR file found in $APP_DIR/lib"
    exit 1
fi

echo "Starting PostgreSQL Template Application..."
echo "Profile: $PROFILE"
echo "Port: $PORT"
echo "Memory: $MEMORY"
echo "JAR: $(basename "$JAR_FILE")"

# Java options
JAVA_OPTS="-Xmx$MEMORY -Xms$MEMORY"
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=$PROFILE"
JAVA_OPTS="$JAVA_OPTS -Dserver.port=$PORT"
JAVA_OPTS="$JAVA_OPTS -Dspring.config.location=classpath:/application.properties,file:$APP_DIR/config/"

# Additional JVM options for production
if [ "$PROFILE" = "prod" ]; then
    JAVA_OPTS="$JAVA_OPTS -server"
    JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC"
    JAVA_OPTS="$JAVA_OPTS -XX:+UseStringDeduplication"
    JAVA_OPTS="$JAVA_OPTS -XX:+OptimizeStringConcat"
    JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom"
fi

# Set log file
LOG_DIR="$APP_DIR/logs"
mkdir -p "$LOG_DIR"
LOG_FILE="$LOG_DIR/application.log"

echo "Log file: $LOG_FILE"
echo "Starting application..."

# Start the application
exec java $JAVA_OPTS -jar "$JAR_FILE" >> "$LOG_FILE" 2>&1