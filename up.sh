#!/bin/bash

set -e  # Exit on any error

# Run the build script
if ! ./build.sh; then
    echo "Build failed, not starting containers"
    exit 1
fi

echo "Starting containers..."
cd docker && docker compose up 