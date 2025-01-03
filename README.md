# Incept5 Supabase Starter

A fullstack project template using Supabase for authentication, Quarkus for the backend API, and prepared for a frontend implementation.

## Project Structure

- `/docker` - Docker setup for Supabase services (PostgreSQL, Kong, Auth)
  - `/docker/kong` - Kong API Gateway configuration
- `/backend` - Quarkus API (Kotlin + Gradle)
  - `/backend/src/main/resources/db/migration` - Flyway database migrations
- `/frontend` - Frontend application (TODO)

## Prerequisites

- Docker and Docker Compose
- Java 17 or later
- Kotlin
- Gradle

## Getting Started

1. Start the Supabase services:
   ```bash
   cd docker
   docker-compose up -d
   ```

2. Run the backend API:
   ```bash
   cd backend
   ./gradlew quarkusDev
   ```

## Services

- **PostgreSQL**: Running on port 5432
- **Kong API Gateway**: Running on ports 8000 (proxy) and 8443 (proxy - SSL)
- **Supabase Auth**: Running on port 9999
- **Quarkus API**: Running on port 8080 (dev mode)

## Development

### Backend Development

The backend is a Quarkus application written in Kotlin using Gradle with the Kotlin DSL. It includes:

- RESTEasy Reactive for REST endpoints
- Hibernate ORM with Panache for Kotlin
- PostgreSQL JDBC driver
- YAML configuration support

To start the backend in development mode:
```bash
cd backend
./gradlew quarkusDev
```

### Database Migrations

Database migrations are handled by Flyway and are now managed by the API container. The migrations are located in `/backend/src/main/resources/db/migration/` and will run automatically when the API container starts up. 