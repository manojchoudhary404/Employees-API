# Employee REST API — Spring Boot + Jenkins CI/CD

A complete Spring Boot REST API for Employee management with H2 in-memory database,
full CRUD endpoints, exception handling, unit + integration tests, Docker support,
and a Jenkins CI/CD pipeline.

---

## Project Structure

```
employee-api/
├── src/
│   ├── main/
│   │   ├── java/com/example/employeeapi/
│   │   │   ├── EmployeeApiApplication.java     ← Main entry point
│   │   │   ├── DataInitializer.java            ← Seeds sample data on startup
│   │   │   ├── controller/
│   │   │   │   └── EmployeeController.java     ← REST endpoints
│   │   │   ├── service/
│   │   │   │   └── EmployeeService.java        ← Business logic
│   │   │   ├── repository/
│   │   │   │   └── EmployeeRepository.java     ← JPA data access
│   │   │   ├── model/
│   │   │   │   └── Employee.java               ← JPA entity
│   │   │   └── exception/
│   │   │       ├── EmployeeNotFoundException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       └── application.properties          ← H2 + JPA config
│   └── test/
│       └── java/com/example/employeeapi/
│           ├── EmployeeServiceTest.java         ← Unit tests (Mockito)
│           └── EmployeeControllerIntegrationTest.java ← Integration tests
├── Dockerfile                                  ← Multi-stage Docker build
├── Jenkinsfile                                 ← CI/CD pipeline (5 stages)
├── pom.xml                                     ← Maven dependencies
└── README.md
```

---

## API Endpoints

| Method   | Endpoint            | Description          | Status Code |
|----------|---------------------|----------------------|-------------|
| GET      | /employees          | Get all employees    | 200         |
| GET      | /employees/{id}     | Get employee by ID   | 200 / 404   |
| POST     | /employees          | Create new employee  | 201         |
| PUT      | /employees/{id}     | Update employee      | 200 / 404   |
| DELETE   | /employees/{id}     | Delete employee      | 204 / 404   |

### Sample JSON Body (POST / PUT)

```json
{
  "name": "John Doe",
  "role": "Developer",
  "salary": 50000
}
```

### Sample 404 Error Response

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 99"
}
```

---

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.9+

### Option 1 — Maven

```bash
mvn spring-boot:run
```

App starts at: http://localhost:8080

### Option 2 — Docker

```bash
# Build image
docker build -t employee-api .

# Run container
docker run -p 8080:8080 employee-api
```

### H2 Console (browser)

Visit: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:employeedb`
- Username: `sa`
- Password: (leave blank)

---

## Running Tests

```bash
mvn test
```

---

## CI/CD Pipeline (Jenkins)

### Jenkins Setup Steps

1. **Install Jenkins plugins**: Git, Maven Integration, Docker Pipeline
2. **Configure tools** in Manage Jenkins → Global Tool Configuration:
   - Maven named `Maven-3.9`
   - JDK named `JDK-17`
3. **Add Docker Hub credentials**:
   - Manage Jenkins → Credentials → Add → Username with password
   - ID: `dockerhub-credentials`
   - Username: your Docker Hub username
   - Password: your Docker Hub password or access token
4. **Edit Jenkinsfile**:
   - Replace `your-dockerhub-username` with your actual username
5. **Create Pipeline job**:
   - New Item → Pipeline
   - Pipeline script from SCM → Git
   - Repository URL: your GitHub repo
   - Script Path: `Jenkinsfile`
6. Click **Build Now** and watch all 5 stages pass ✅

### Pipeline Stages

| Stage | What it does |
|-------|-------------|
| Checkout Code | Pulls latest code from GitHub |
| Build Application | `mvn clean package -DskipTests` → creates JAR |
| Run Tests | `mvn test` → runs all unit + integration tests |
| Build Docker Image | Builds image tagged with build number + latest |
| Push to Docker Hub | Logs in securely, pushes both tags |

---

## Docker Hub

After a successful pipeline, your image will be at:
```
docker pull your-dockerhub-username/employee-api:latest
```
