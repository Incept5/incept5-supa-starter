#!/bin/bash

set -e  # Exit on any error

# Parse command line arguments
DETACH_FLAG=""
while getopts "d" flag; do
    case "${flag}" in
        d) DETACH_FLAG="--detach" ;;
    esac
done

# Run the build script
if ! ./build.sh; then
    echo "Build failed, not starting containers"
    exit 1
fi

echo "Starting containers..."
cd docker && docker compose up $DETACH_FLAG 