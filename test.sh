#!/bin/bash

set -e  # Exit on any error

echo "Testing backend..."
if ! (cd backend && ./gradlew clean test --info); then
    echo "Building and testing backend failed!"
    exit 1
fi

exit 0 