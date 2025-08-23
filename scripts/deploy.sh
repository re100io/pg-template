#!/bin/bash

# PostgreSQL Template Application Deployment Script
set -e

echo "Deploying PostgreSQL Template Application..."

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_DIR"

# Configuration
ENVIRONMENT="${1:-prod}"
BUILD_IMAGE="${2:-true}"

echo "Environment: $ENVIRONMENT"
echo "Build Image: $BUILD_IMAGE"

# Build release package
if [ "$BUILD_IMAGE" = "true" ]; then
    echo "Building release package..."
    ./scripts/build-release.sh
    
    echo "Building Docker image..."
    docker build -t pg-template:latest .
fi

# Create necessary directories
mkdir -p logs config

# Copy configuration files
echo "Copying configuration files..."
cp src/main/resources/application-prod.properties config/ 2>/dev/null || echo "Warning: application-prod.properties not found"

# Deploy with Docker Compose
echo "Deploying with Docker Compose..."
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d

# Wait for services to be ready
echo "Waiting for services to be ready..."
sleep 30

# Health check
echo "Performing health check..."
for i in {1..30}; do
    if curl -f http://localhost:7001/api/health > /dev/null 2>&1; then
        echo "✅ Application is healthy!"
        break
    fi
    echo "Waiting for application to be ready... ($i/30)"
    sleep 5
done

# Show status
echo ""
echo "Deployment Status:"
docker-compose -f docker-compose.prod.yml ps

echo ""
echo "Application URLs:"
echo "  - Health Check: http://localhost:7001/api/health"
echo "  - API Documentation: http://localhost:7001/swagger-ui.html"

echo ""
echo "Logs:"
echo "  - Application: docker-compose -f docker-compose.prod.yml logs -f app"
echo "  - Database: docker-compose -f docker-compose.prod.yml logs -f postgres"