# Development Guidelines

## Project Structure and Architecture

### Backend Module Organization
```
api/modules/{module-name}/
  ├── controller/     # REST endpoints
  ├── service/        # Business logic
  ├── repository/     # Data access
  ├── domain/        # Domain entities
  └── dto/           # Data transfer objects
```

### Service Layer Guidelines
- Each service class should be focused on a single responsibility
- Services should be in separate files
- Use constructor injection for dependencies
- Implement clear interface boundaries

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
- Error response format:
  ```json
  {
    "error": "ResourceNotFoundException",
    "message": "Widget not found with ID: 123",
    "status": 404
  }
  ```

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

## Testing Guidelines

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

## Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Keep functions small and focused
- Document public APIs and complex logic
- Use nullable types appropriately

## Common Patterns

- Use DTOs for API requests/responses
- Implement proper error handling and custom exceptions
- Follow RESTful API conventions
- Use dependency injection

## Tools and Commands

- Use `pnpm` instead of `npm` for frontend dependencies
- Run tests after code changes to backend
  ```bash
  cd ../backend && ./gradlew clean test | cat
  ```
If you see a test failure then look in the index.html that is generated for the cause of the failure:
backend/build/reports/tests/test/index.html

- Start development environment:
  ```bash
  cd docker && docker-compose up -d
  cd ../backend && ./gradlew quarkusDev
  ``` 