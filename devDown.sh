#!/bin/bash

set -e  # Exit on any error

echo "Stopping all development containers..."
cd docker && docker compose -f docker-compose.yml -f dev/docker-compose.dev.yml down 