# Deployment API

REST API for tracking deployment events built with Spring Boot and Java 17.

## Quick Start 

### Prerequisites
You will need **[Java 17+](https://adoptium.net/)** and **[Maven 3.6+](https://maven.apache.org/download.cgi)**.

You can download them directly from the links above, or install them instantly using your package manager:

**macOS (Homebrew)**
```bash
brew install openjdk@17 maven
```

**Linux (Ubuntu/Debian)**
```bash
sudo apt update
sudo apt install openjdk-17-jdk maven
```

**Windows (Chocolatey)**
```powershell
choco install openjdk17 maven
```

### Running the Application

```bash
# Navigate to project directory
cd deployment-api

# Build and run in one command
mvn clean spring-boot:run
```

**That's it!** The server will start on `http://localhost:8080` with 35 deployment events automatically seeded.

## Testing the API

Once running, try these commands:

```bash
# Get all deployments (returns 35 events)
curl http://localhost:8080/deployments | jq

# Filter by service
curl "http://localhost:8080/deployments?service=billing-api" | jq

# Filter by status (success, failed, in_progress, rolled_back)
curl "http://localhost:8080/deployments?status=failed" | jq

# Get a specific deployment
curl http://localhost:8080/deployments/deploy_001 | jq

# Test error handling (404)
curl http://localhost:8080/deployments/unknown_id | jq

# Test error handling (400 - invalid status)
curl "http://localhost:8080/deployments?status=badvalue" | jq
```

## API Documentation

### 1. List Deployments

**Endpoint:** `GET /deployments`

**Query Parameters:**
- `service` (optional) - Filter by service name (e.g., `billing-api`, `user-service`)
- `status` (optional) - Filter by deployment status: `SUCCESS`, `FAILED`, `IN_PROGRESS`, `ROLLED_BACK`

**Response:** `200 OK`
```json
{
  "data": [
    {
      "id": "deploy_001",
      "service": "billing-api",
      "status": "SUCCESS",
      "duration": 150,
      "timestamp": "2025-04-01T08:00:00Z",
      "commit_sha": "a1b2c3d"
    }
  ],
  "count": 35
}
```

**Examples:**
```bash
# All deployments
GET /deployments

# Filter by service
GET /deployments?service=billing-api

# Filter by status
GET /deployments?status=failed

# Combine filters
GET /deployments?service=user-service&status=success
```

### 2. Get Deployment by ID

**Endpoint:** `GET /deployments/{id}`

**Path Parameters:**
- `id` (required) - Deployment ID (e.g., `deploy_001`)

**Response:** `200 OK`
```json
{
  "id": "deploy_001",
  "service": "billing-api",
  "status": "SUCCESS",
  "duration": 150,
  "timestamp": "2025-04-01T08:00:00Z",
  "commit_sha": "a1b2c3d"
}
```

**Error Response:** `404 Not Found`
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Deployment not found with id: unknown_id",
  "timestamp": "2025-04-28T14:32:00Z",
  "path": "/deployments/unknown_id"
}
```

## Error Handling

The API returns consistent error responses with appropriate HTTP status codes:

### 400 Bad Request
Invalid query parameters or malformed requests.

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid status value: 'badvalue'. Allowed values: [SUCCESS, FAILED, IN_PROGRESS, ROLLED_BACK]",
  "timestamp": "2025-04-28T14:32:00Z",
  "path": "/deployments"
}
```

### 404 Not Found
Deployment ID does not exist.

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Deployment not found with id: deploy_999",
  "timestamp": "2025-04-28T14:32:00Z",
  "path": "/deployments/deploy_999"
}
```

### 500 Internal Server Error
Unexpected server errors (properly logged for debugging).

## Sample Data

The application automatically seeds **35 deployment events** on startup:

### Statuses (4)
- `SUCCESS` - Deployment completed successfully
- `FAILED` - Deployment failed
- `IN_PROGRESS` - Deployment currently running
- `ROLLED_BACK` - Deployment was rolled back

### Time Range
April 2025 - June 2025 (distributed across multiple days)

### Key Design Decisions

**Why In-Memory Storage?**
- Zero setup time - runs immediately
- Thread-safe via `ConcurrentHashMap`
- Easy migration path to real database via interface


**Why Enums for Status?**
- Type-safe at compile time
- Prevents invalid values in domain layer
- Centralized valid values

**Why Interface-Driven Design?**
- Enables easy testing with mocks
- Supports swapping implementations
- Documents contracts clearly

## Project Structure

```
deployment-api/
├── pom.xml                                    # Maven configuration
├── README.md                                  # This file
└── src/
    ├── main/
    │   ├── java/com/deploymentapi/
    │   │   ├── DeploymentApiApplication.java      # Spring Boot entry point
    │   │   ├── controller/
    │   │   │   └── DeploymentController.java      # REST endpoints (thin layer)
    │   │   ├── service/
    │   │   │   ├── DeploymentService.java         # Service interface
    │   │   │   └── DeploymentServiceImpl.java     # Business logic + validation
    │   │   ├── repository/
    │   │   │   ├── DeploymentRepository.java      # Storage interface
    │   │   │   └── InMemoryDeploymentRepository.java  # In-memory implementation
    │   │   ├── model/
    │   │   │   ├── Deployment.java                # Core domain entity
    │   │   │   └── DeploymentStatus.java          # Status enum
    │   │   ├── dto/
    │   │   │   ├── DeploymentResponse.java        # API response object
    │   │   │   ├── DeploymentListResponse.java    # List wrapper
    │   │   │   └── DeploymentFilter.java          # Query filter
    │   │   ├── exception/
    │   │   │   ├── DeploymentApiException.java    # Base exception
    │   │   │   ├── DeploymentNotFoundException.java   # 404 exception
    │   │   │   ├── InvalidFilterException.java    # 400 exception
    │   │   │   └── GlobalExceptionHandler.java    # Centralized error handling
    │   │   └── seed/
    │   │       └── DeploymentDataSeeder.java      # Auto-seeds 35 events
    │   └── resources/
    │       └── application.properties             # Server configuration
    └── test/
        └── java/com/deploymentapi/
            └── DeploymentServiceTest.java         # Service layer tests
```

## Development

### Build the Project

```bash
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Package as JAR

```bash
mvn clean package
```

### Run the JAR

```bash
java -jar target/deployment-api-1.0.0.jar
```

### Change Server Port

Edit `src/main/resources/application.properties`:

```properties
server.port=9090
```

Or pass as command-line argument:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

## Technology Stack

- **Java 17** - Modern Java LTS version
- **Spring Boot 3.2.0** - Production-ready framework
- **Spring Web** - RESTful web services
- **Jackson** - JSON serialization/deserialization
- **Maven** - Build and dependency management
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework for tests

## Troubleshooting

### Port 8080 Already in Use

```bash
# Find process using port 8080
lsof -i:8080

# Kill the process
kill -9 <PID>

# Or change the port in application.properties
server.port=9090
```

### Java Version Mismatch

Ensure Java 17 is installed and active:

```bash
java -version
# Should show version 17.x.x
```

### Maven Build Fails

```bash
# Clean and rebuild
mvn clean install

# Skip tests if needed
mvn clean install -DskipTests
```

## License

This project is created for demonstration purposes.

## Contact

For questions or issues, please refer to the project documentation or create an issue in the repository.

---

**Built with ❤️ using Spring Boot and Java 17**

