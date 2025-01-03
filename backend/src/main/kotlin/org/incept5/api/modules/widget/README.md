# Widget Module (Example)

This is an example module demonstrating the standard project structure and patterns used in this application. 
**This module can be safely removed once real API modules are implemented.**

## Purpose

The Widget module serves as a reference implementation showing:
- How to structure a module following the project's conventions
- How to implement CRUD operations with pagination and sorting
- How to handle authentication and authorization with Supabase
- How to implement proper validation and error handling
- How to write integration tests

## Features Demonstrated

### Architecture
- Standard module structure (controller/service/repository/domain/dto)
- Clean separation of concerns
- Dependency injection
- Repository pattern with Panache

### Security
- JWT authentication with Supabase
- Resource ownership validation
- Input validation
- CORS configuration

### Data Management
- CRUD operations
- Pagination and sorting
- Optimistic locking
- ULID for entity IDs
- UUID for user references

### Error Handling
- Custom exceptions
- Validation error responses
- Proper HTTP status codes
- Consistent error format

### Testing
- Integration tests with real database
- Authentication in tests
- Test data management
- Error case testing

## Module Structure
```
widget/
├── controller/     # REST endpoints
│   └── WidgetController.kt
├── service/       # Business logic
│   └── WidgetService.kt
├── repository/    # Data access
│   └── WidgetRepository.kt
├── domain/       # Domain entities
│   ├── Widget.kt
│   └── WidgetCategory.kt
└── dto/          # Data transfer objects
    └── WidgetDtos.kt
```

## API Endpoints

### Create Widget
```http
POST /api/widgets
Authorization: Bearer {jwt}
Content-Type: application/json

{
  "description": "Example Widget",
  "category": "BASIC",
  "level": 1
}
```

### Update Widget
```http
PUT /api/widgets/{widgetId}
Authorization: Bearer {jwt}
Content-Type: application/json

{
  "description": "Updated Widget",
  "category": "PREMIUM",
  "level": 2,
  "version": 0
}
```

### Get Widget
```http
GET /api/widgets/{widgetId}
Authorization: Bearer {jwt}
```

### List Widgets
```http
GET /api/widgets?page=0&size=20&sort=createdAt&direction=DESC&category=BASIC
Authorization: Bearer {jwt}
```

### Delete Widget
```http
DELETE /api/widgets/{widgetId}
Authorization: Bearer {jwt}
```

## Error Responses

### Not Found
```json
{
  "error": "ResourceNotFoundException",
  "message": "Widget not found with ID: 123",
  "status": 404
}
```

### Validation Error
```json
{
  "error": "Validation Error",
  "status": 400,
  "violations": [
    {
      "field": "description",
      "message": "Description must be between 3 and 1000 characters",
      "invalidValue": ""
    }
  ]
}
```

### Optimistic Lock Error
```json
{
  "error": "OptimisticLockException",
  "message": "Resource has been modified by another user",
  "status": 409
}
``` 