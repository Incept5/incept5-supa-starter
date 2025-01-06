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

### Frontend Module Organization
```
frontend/src/
  ├── components/     # Reusable React components
  ├── pages/         # Page components and routing
  ├── lib/           # Utilities and shared code
  ├── hooks/         # Custom React hooks
  ├── i18n/          # Internationalization files
  └── styles/        # Global styles and Tailwind config
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

## Frontend Guidelines

### Component Best Practices
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

### Styling Guidelines
- Use Tailwind CSS for styling
- Follow the project's color scheme and design tokens
- Use the `cn()` utility for conditional classes
- Keep styles modular and reusable
- Use responsive design patterns

### Internationalization
- Use translation keys for all user-facing text
- Keep translation files organized by feature
- Use the `useTranslation` hook consistently
- Example:
  ```typescript
  const { t } = useTranslation();
  return <h1>{t('page.title')}</h1>;
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

### Backend Testing
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

### Frontend Testing
- Write unit tests for components using Vitest
- Use React Testing Library for component testing
- Test user interactions and state changes
- Mock API calls and external dependencies
- Example:
  ```typescript
  test('button click triggers action', async () => {
    const handleClick = vi.fn();
    render(<Button label="Click me" onClick={handleClick} />);
    await userEvent.click(screen.getByText('Click me'));
    expect(handleClick).toHaveBeenCalled();
  });
  ```

## Code Style

- Follow Kotlin/TypeScript coding conventions
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
- Run tests after code changes:
  ```bash
  # Backend tests
  cd backend && ./gradlew clean test | cat

  # Frontend tests
  cd frontend && pnpm test
  ```
If you see a test failure then look in the index.html that is generated for the cause of the failure:
backend/build/reports/tests/test/index.html

- Start development environment:
  ```bash
  # Start backend services
  cd docker && docker-compose up -d
  cd ../backend && ./gradlew quarkusDev

  # Start frontend development server
  cd ../frontend && pnpm dev
  ``` 