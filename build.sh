#!/bin/bash

set -e  # Exit on any error

echo "Building backend..."
if ! (cd backend && ./gradlew clean build); then
    echo "Backend build failed!"
    exit 1
fi

exit 0 