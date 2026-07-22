# Agent Instructions for Deployment API Project

## Persona
You are an expert Java Spring Boot developer working on the Deployment API project. You follow clean architecture principles, SOLID design patterns, and Spring Boot best practices.

## Project Overview
This is a Spring Boot REST API application for managing deployments. The application uses an in-memory repository and follows a layered architecture pattern.

---

## Architecture Guidelines

### Layered Architecture
Always follow this strict layered architecture:

```
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

**Rules:**
- Controllers should **only** handle HTTP concerns (request/response mapping, HTTP status codes)
- Business logic belongs **only** in Services
- Repositories should **only** interact with the database
- Never skip layers - always go through the proper chain

---

## Core Principles

### 1. DTOs Over Entities
- **Never** expose Entity classes directly in APIs
- **Always** use Request DTOs for incoming data
- **Always** use Response DTOs for outgoing data
- Keep DTOs in the `dto` package
- DTOs should be immutable where possible
- **Use Java records** for DTOs to avoid boilerplate code (getters, setters, equals, hashCode, toString)

Example:
```java
public record DeploymentResponse(
    String id,
    String name,
    DeploymentStatus status,
    LocalDateTime createdAt
) {}

public record DeploymentRequest(
    @NotBlank(message = "Name is required")
    String name,
    @NotNull(message = "Status is required")
    DeploymentStatus status
) {}
```

### 2. Dependency Injection
- Use **constructor injection only**
- Never use field injection (`@Autowired` on fields)
- Let Spring handle dependency management through constructor parameters

Example:
```java
@Service
public class MyService {
    private final MyRepository repository;
    
    public MyService(MyRepository repository) {
        this.repository = repository;
    }
}
```

### 4. Exception Handling
- Use global exception handling with `@ControllerAdvice`
- Create custom exceptions that extend `RuntimeException`
- Keep all exception handlers in the `exception` package
- Return appropriate HTTP status codes with meaningful error messages
- Use `@ExceptionHandler` methods for specific exception types

### 5. Logging
- Use **SLF4J** for all logging
- Import: `import org.slf4j.Logger;` and `import org.slf4j.LoggerFactory;`
- Create logger instance: `private static final Logger log = LoggerFactory.getLogger(ClassName.class);`
- Log at appropriate levels:
  - `ERROR`: Errors and exceptions
  - `WARN`: Warning conditions
  - `INFO`: Important business process information
  - `DEBUG`: Detailed information for debugging
- Log method entry/exit for complex operations
- Always log exceptions with stack traces

Example:
```java
private static final Logger log = LoggerFactory.getLogger(MyService.class);

public void doSomething() {
    log.info("Starting doSomething operation");
    try {
        // business logic
        log.debug("Successfully completed doSomething");
    } catch (Exception e) {
        log.error("Error in doSomething: {}", e.getMessage(), e);
        throw e;
    }
}
```

### 6. Validation
- Validate **all** incoming requests using Bean Validation
- Use annotations like `@NotNull`, `@NotBlank`, `@Size`, `@Valid`, etc.
- Add `@Valid` or `@Validated` to controller method parameters
- Create custom validators when needed
- Handle validation errors in global exception handler

Example:
```java
public class DeploymentRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Status is required")
    private DeploymentStatus status;
}

@PostMapping
public ResponseEntity<DeploymentResponse> create(@Valid @RequestBody DeploymentRequest request) {
    // ...
}
```

---

## REST API Guidelines

### Naming Conventions
- Use **plural nouns** for resource names: `/deployments`, not `/deployment`
- Use **kebab-case** for multi-word resources: `/deployment-configs`
- Use **path parameters** for identifiers: `/deployments/{id}`
- Use **query parameters** for filtering/sorting: `/deployments?status=RUNNING&sort=createdAt`

### HTTP Methods
- `GET` - Retrieve resource(s)
- `POST` - Create new resource
- `PUT` - Update entire resource
- `PATCH` - Partially update resource
- `DELETE` - Delete resource

### HTTP Status Codes
- `200 OK` - Successful GET, PUT, PATCH
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Validation error
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

### Response Format
- Return appropriate Response DTOs
- Wrap collections in a response object with metadata
- Include meaningful error messages in error responses

---

## Package Structure

```
com.deploymentapi
├── config/              # Configuration classes
├── controller/          # REST controllers
├── dto/                 # Request and Response DTOs
├── exception/           # Custom exceptions and handlers
├── model/               # Entity/Domain models
├── repository/          # Data access layer
├── seed/                # Data seeding utilities
└── service/             # Business logic layer
```

---

## Coding Standards

### General
- Use meaningful variable and method names
- Keep methods small and focused (single responsibility)
- Avoid code duplication (DRY principle)
- Comment only when necessary to explain "why", not "what"
- Use Java naming conventions (camelCase, PascalCase)

### Spring Boot Annotations
- `@RestController` for REST controllers
- `@Service` for service classes
- `@Repository` for repository classes
- `@Component` for generic components
- `@Configuration` for configuration classes
- Use `@RequestMapping` at class level for base path
- Use specific annotations (`@GetMapping`, `@PostMapping`, etc.) at method level

### Method Structure
1. Validate input
2. Log important operations
3. Execute business logic
4. Handle exceptions
5. Return appropriate response

---

## Testing Guidelines

- Write unit tests for all service methods
- Use JUnit 5 and Mockito
- Mock dependencies in unit tests
- Aim for high test coverage
- Test happy path and error scenarios
- Use meaningful test names: `shouldReturnDeploymentWhenIdExists()`

---

## Security Considerations

- Never log sensitive information
- Validate all user input
- Use parameterized queries (protect against injection)
- Handle errors gracefully without exposing internal details
- Follow principle of least privilege

---

## Additional Notes

- Keep configuration in `application.properties`
- Use Spring profiles for environment-specific configuration
- Follow semantic versioning for API versions
- Document APIs with clear javadoc comments
- Keep dependencies up to date

---

## Example Structure

### Controller Example
```java
@RestController
@RequestMapping("/api/v1/deployments")
public class DeploymentController {
    private static final Logger log = LoggerFactory.getLogger(DeploymentController.class);
    private final DeploymentService deploymentService;
    
    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DeploymentResponse> getById(@PathVariable String id) {
        log.info("Fetching deployment with id: {}", id);
        DeploymentResponse response = deploymentService.getById(id);
        return ResponseEntity.ok(response);
    }
}
```

### Service Example
```java
@Service
public class DeploymentServiceImpl implements DeploymentService {
    private static final Logger log = LoggerFactory.getLogger(DeploymentServiceImpl.class);
    private final DeploymentRepository repository;
    
    public DeploymentServiceImpl(DeploymentRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public DeploymentResponse getById(String id) {
        log.debug("Retrieving deployment with id: {}", id);
        Deployment deployment = repository.findById(id)
            .orElseThrow(() -> new DeploymentNotFoundException("Deployment not found with id: " + id));
        return mapToResponse(deployment);
    }
}
```

### Repository Example
```java
public interface DeploymentRepository {
    Optional<Deployment> findById(String id);
    List<Deployment> findAll();
    Deployment save(Deployment deployment);
    void deleteById(String id);
}
```

---

## Remember
- **Consistency is key** - Follow these patterns throughout the entire codebase
- **When in doubt**, refer back to these architectural guidelines
- **Quality over speed** - Write clean, maintainable code
- **Think before coding** - Consider the impact on the overall architecture

---

*Last Updated: July 23, 2026*

