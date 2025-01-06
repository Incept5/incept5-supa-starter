# Incept5 Supabase Starter

A fullstack project template using Supabase for authentication, Quarkus for the backend API, and React for the frontend.

## Project Structure

- `/docker` - Docker setup for Supabase services (PostgreSQL, Kong, Auth)
  - `/docker/kong` - Kong API Gateway configuration
- `/backend` - Quarkus API (Kotlin + Gradle)
  - `/backend/src/main/resources/db/migration` - Flyway database migrations
- `/frontend` - React frontend application
  - Built with Vite, React, TypeScript, and Tailwind CSS
  - Uses shadcn/ui components
  - Includes i18n support
  - Integrated with Supabase Auth

## Prerequisites

- Docker and Docker Compose
- Java 17 or later
- Kotlin
- Gradle
- Node.js 18 or later
- pnpm (preferred over npm)

## Root Scripts

The project includes three main scripts in the root directory:

- `devUp.sh` - Starts the development environment with a container-based database. Data is stored within the container and will be lost when the container is removed.
- `up.sh` - Starts the environment with persistent database storage. Data is stored on the host machine under `docker/volumes/db` and persists between container restarts/removals.
- `build.sh` - Builds the project (details to be added).

## Services

- **PostgreSQL**: Running on port 5432
- **Kong API Gateway**: Running on ports 8000 (proxy) and 8443 (proxy - SSL)
- **Supabase Auth**: Running on port 9999
- **Quarkus API**: Running on port 8080 (dev mode)
- **Frontend Dev Server**: Running on port 5173 (dev mode)

## Development

### Backend Development

The backend is a Quarkus application written in Kotlin using Gradle with the Kotlin DSL. It includes:

- RESTEasy Reactive for REST endpoints
- Hibernate ORM with Panache for Kotlin
- PostgreSQL JDBC driver
- YAML configuration support

To start the backend in development mode (first stop the quarkus-api container to prevent port conflicts and then run this script):
```bash
cd backend
./gradlew quarkusDev
```

### Frontend Development

The frontend is a modern React application built with:
- Vite for fast development and optimized builds
- React with TypeScript for type safety
- Tailwind CSS for styling
- shadcn/ui for beautiful, accessible components
- i18n for internationalization
- Supabase Auth integration

To start the frontend in development mode:
```bash
cd frontend
pnpm install
pnpm dev
```

### Database Migrations

Database migrations are handled by Flyway and are now managed by the API container. The migrations are located in `/backend/src/main/resources/db/migration/` and will run automatically when the API container starts up. 