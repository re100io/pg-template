#!/bin/bash

# PostgreSQL Template Application Release Build Script

set -e

echo "Building PostgreSQL Template Application Release..."

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

# Clean previous builds
echo "Cleaning previous builds..."
mvn clean

# Run tests
echo "Running tests..."
mvn test -Ptest

# Build release package
echo "Building release package..."
mvn package -Prelease -DskipTests=false

# Check if build was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Release build completed successfully!"
    echo ""
    echo "Generated files:"
    find target -name "*.jar" -o -name "*.tar.gz" -o -name "*.zip" | while read file; do
        echo "  - $(basename "$file") ($(du -h "$file" | cut -f1))"
    done
    echo ""
    echo "To run the application:"
    echo "  java -jar target/pg-template-1.0-SNAPSHOT.jar --spring.profiles.active=prod"
    echo ""
    echo "Or extract the distribution package and use:"
    echo "  ./bin/start.sh prod 7001"
else
    echo "❌ Release build failed!"
    exit 1
fi