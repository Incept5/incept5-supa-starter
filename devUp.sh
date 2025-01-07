#!/bin/bash

set -e  # Exit on any error

# Parse command line arguments
DETACH_FLAG=""
NO_QUARKUS=false

while getopts "d-:" opt; do
    case $opt in
        d)
            DETACH_FLAG="--detach"
            ;;
        -)
            case "${OPTARG}" in
                no-quarkus)
                    NO_QUARKUS=true
                    ;;
                *)
                    echo "Invalid option: --${OPTARG}" >&2
                    exit 1
                    ;;
            esac
            ;;
    esac
done

# Run the build script
if ! ./build.sh; then
    echo "Build failed, not starting containers"
    exit 1
fi

if [ "$NO_QUARKUS" = true ]; then
    echo "Starting containers without Quarkus (run Quarkus in dev mode manually)..."
    echo "To start Quarkus in dev mode, run:"
    echo "cd backend && ./gradlew quarkusDev"
    cd docker && docker compose -f docker-compose.yml -f dev/docker-compose.dev.yml up --scale quarkus-api=0 $DETACH_FLAG
else
    echo "Starting containers with dev configuration..."
    cd docker && docker compose -f docker-compose.yml -f dev/docker-compose.dev.yml build quarkus-api && docker compose -f docker-compose.yml -f dev/docker-compose.dev.yml up $DETACH_FLAG 
fi 