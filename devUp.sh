#!/bin/bash

set -e  # Exit on any error

# Run the build script
if ! ./build.sh; then
    echo "Build failed, not starting containers"
    exit 1
fi

echo "Starting containers with dev configuration..."
cd docker && docker compose -f docker-compose.yml -f dev/docker-compose.dev.yml up 