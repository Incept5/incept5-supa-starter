# Development Guidelines

This document outlines the development guidelines for both backend and frontend components of the project. The guidelines are split into two main sections for clarity, but are maintained in a single document to ensure consistency in development practices across the entire stack.

---

# Backend Guidelines

## Module Organization
```
api/modules/{module-name}/
  ├── controller/     # REST endpoints
  ├── service/        # Business logic
  ├── repository/     # Data access
  ├── domain/        # Domain entities
  └── dto/           # Data transfer objects
```

## API-First Development

### Core Principles
- The backend API is the source of truth
- Never modify API contracts to accommodate frontend needs
- All API changes must be backwards compatible
- If breaking changes are needed, stop development and:
  1. Document the breaking changes needed
  2. Explain why they cannot be made backwards compatible
  3. Get explicit user confirmation before proceeding

### API Design Rules
- Use string representations instead of enums in responses (but keep the enum in the code and in the request definition)
  - This allows adding new values without breaking existing clients
- Use versioned DTOs for request/response objects
  - Allows adding new optional fields without breaking existing clients
  - Never remove or modify existing fields
- Use nullable fields for optional data instead of omitting them
  - Consistent response structure helps clients
  - Makes it clear what fields might be missing
- Prefer flat object structures over deep nesting
  - Easier to add fields without breaking changes
  - Better for partial updates
- Use ISO 8601 for all date/time fields
  - Include timezone information
  - Consistent format: `YYYY-MM-DDThh:mm:ss.sssZ`
- Include total count in paginated responses
  - Helps clients implement pagination correctly
  - Example:
    ```json
    {
      "items": [],
      "totalCount": 42,
      "page": 0,
      "size": 20
    }
    ```
- Use descriptive error responses
  - Include error code, message, and details
  - Document all possible error codes
  - Example:
    ```json
    {
      "code": "INVALID_INPUT",
      "message": "Invalid input provided",
      "details": {
        "field": "email",
        "reason": "must be a valid email address"
      }
    }
    ```

### Versioning Strategy
- Use URL versioning for major breaking changes (v1, v2)
- Use request/response DTOs for minor changes
- Maintain compatibility within a major version
- Document deprecation plans and timelines

## Service Layer Guidelines
- Each service class should be focused on a single responsibility
- Services should be in separate files
- Use constructor injection for dependencies
- Implement clear interface boundaries

## Authentication and Users

### Overview
- Authentication is handled by Supabase
- JWT tokens are used for authentication
- The subject claim in the JWT corresponds to `auth.users.id`
- User identification is via UUID from Supabase auth

### Implementation Guidelines
- Use `userId` as the field name when referencing the Supabase user ID
- The `userId` field should be of type UUID
- Do NOT add foreign key constraints to the `auth.users` table
- Always include `Authorization: Bearer <token>` header for authenticated requests

### Accessing User ID in Controllers
- Use `@Authenticated` annotation on controllers/endpoints requiring authentication
- Get the user ID from the security context:
  ```kotlin
  @Context securityContext: SecurityContext
  val userId = securityContext.userPrincipal.name  // Returns the Supabase auth.users.id
  ```
- Example authenticated endpoint:
  ```kotlin
  @POST
  @Authenticated
  fun createWidget(
      @Valid request: CreateWidgetRequest,
      @Context securityContext: SecurityContext
  ): Response {
      val userId = securityContext.userPrincipal.name
      // Use userId in service calls
  }
  ```

### Security Best Practices
- All authenticated endpoints must validate the JWT token
- Use `@QuarkusTest` and `BaseAuthenticatedTest` for testing secured endpoints
- Log authentication events at appropriate levels:
  - DEBUG for token extraction and validation steps
  - ERROR for authentication failures
  - INFO for significant auth events

### Example Entity with User Reference
```kotlin
@Entity
class Widget {
    @Column(name = "user_id")
    lateinit var userId: UUID  // References auth.users.id
    
    // ... other fields
}
```

## Documentation Guidelines

### Code Documentation
- Add Javadoc to all core classes and interfaces
- Document non-obvious methods and complex logic
- Include example JSON responses in API-related documentation
- Document error cases and expected exceptions
- Use clear and concise language
- Example:
  ```kotlin
  /**
   * Processes user registration requests.
   * Validates the input, ensures unique email, and creates a new user account.
   *
   * @param request Registration details including email and password
   * @return Created user details
   * @throws InvalidRequestException if email is already in use
   */
  fun registerUser(request: RegisterRequest): UserResponse
  ```

### Error Handling
- Use appropriate exception types from `org.incept5.api.core.error`
- Provide meaningful error messages
- Include relevant context in error responses
- Log errors with appropriate level (ERROR for exceptions, WARN for business rule violations)

## Database and Entity Guidelines

### Database Changes
- All database changes must be made through Flyway migrations
- Migrations located in `backend/src/main/resources/db/migration`
- Use descriptive names: `V{version}__{description}.sql`
- Keep migrations simple and focused
- NO TRIGGERS - handle all data modifications in the application layer
- Use appropriate indexes for foreign keys and commonly queried fields
- Always include proper foreign key constraints
- Use UUID type for IDs when referencing Supabase auth.users

### Entity Requirements
- Use ULID for entity IDs (via `UlidGenerator`)
- Implement version property for optimistic locking on important entities
- Example:
  ```kotlin
  @Version
  var version: Long? = null
  
  @Column(name = "id")
  var id: String = UlidGenerator.generate()
  ```

## Logging Guidelines

### Logging Levels
- INFO: Important business events, state transitions, API calls
- DEBUG: Detailed information for troubleshooting
- WARN: Business rule violations, deprecated usage
- ERROR: Exceptions and error conditions
  
### Logging Best Practices
- Log entry and exit points of significant operations
- Include relevant context (IDs, user info, etc.)
- Use structured logging where possible
- Example:
  ```kotlin
  logger.info("Creating new user: email={}", email)
  logger.debug("User details: {}", userDetails)
  ```

## Backend Testing
- Write integration tests using real dependencies (no mocks)
- Use `@QuarkusTest` for integration tests
- Test both success and error scenarios
- Include appropriate test data setup
- Extend `BaseAuthenticatedTest` for tests requiring authentication
- Use `ensureTestUsers()` or `ensureUser()` for test user setup

For a comprehensive example of integration testing, see `backend/src/test/kotlin/org/incept5/api/modules/widget/WidgetIntegrationTest.kt`. This test demonstrates:
- End-to-end testing without mocks
- Authentication handling
- Validation testing
- Test data setup
- Testing both success and error scenarios

## Backend Code Style and Patterns
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Keep functions small and focused
- Document public APIs and complex logic
- Use nullable types appropriately
- Use DTOs for API requests/responses
- Implement proper error handling and custom exceptions
- Follow RESTful API conventions
- Use dependency injection

---

# Frontend Guidelines

## Module Organization
```
frontend/src/
  ├── components/     # Reusable React components
  ├── pages/         # Page components and routing
  ├── lib/           # Utilities and shared code
  ├── hooks/         # Custom React hooks
  ├── i18n/          # Internationalization files
  └── styles/        # Global styles and Tailwind config
```

## Component Best Practices
- Use TypeScript for all components and files
- Keep components small and focused
- Use shadcn/ui components for consistent UI
- Implement proper prop types and interfaces
- Use React hooks for state management
- Example:
  ```typescript
  interface ButtonProps {
    label: string;
    onClick: () => void;
    variant?: 'primary' | 'secondary';
  }

  export function Button({ label, onClick, variant = 'primary' }: ButtonProps) {
    return (
      <button
        onClick={onClick}
        className={cn(
          'px-4 py-2 rounded',
          variant === 'primary' ? 'bg-primary text-white' : 'bg-secondary text-black'
        )}
      >
        {label}
      </button>
    );
  }
  ```

## Styling Guidelines
- Use Tailwind CSS for styling
- Follow the project's color scheme and design tokens
- Use the `cn()` utility for conditional classes
- Keep styles modular and reusable
- Use responsive design patterns

## Internationalization
- Use translation keys for all user-facing text
- Keep translation files organized by feature
- Use the `useTranslation` hook consistently
- Example:
  ```typescript
  const { t } = useTranslation();
  return <h1>{t('page.title')}</h1>;
  ```

## Frontend Testing
- Write unit tests for components using Vitest
- Use React Testing Library for component testing
- Test user interactions and state changes
- Mock API calls and external dependencies

## Frontend Code Style
- Follow TypeScript coding conventions
- Use meaningful variable and function names
- Keep components and functions small and focused
- Document complex logic and component APIs
- Use TypeScript types appropriately

---

# Tools and Commands

## Backend Commands
```bash
# Run backend tests
cd backend && ./gradlew clean test | cat

# Start backend services
cd docker && docker-compose up -d
cd ../backend && ./gradlew quarkusDev
```
If you see a test failure then look in the index.html that is generated for the cause of the failure:
backend/build/reports/tests/test/index.html

## Frontend Commands
```bash
# Run frontend tests
cd frontend && pnpm test

# Start frontend development server
cd frontend && pnpm dev
```

## General Notes
- Use `pnpm` instead of `npm` for frontend dependencies
- Run tests after code changes
- Always check test reports for failures 