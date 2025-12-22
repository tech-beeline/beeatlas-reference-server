# Open Source Reference Service

Reference service for managing users, products, business capabilities, and technical capabilities. Built on Spring Boot 2.7.3 using PostgreSQL as the database.

## Description

Open Source Reference Service is a REST API service that provides functionality for managing reference data in enterprise architecture. The service supports management of:

- **Users** - user creation and password management
- **Products** - product management with Structurizr integration
- **Business Capabilities** - business capability management with hierarchy support
- **Technical Capabilities** - technical capability management and their relationships

## Technology Stack

- **Java 17**
- **Spring Boot 2.7.3**
- **PostgreSQL 15**
- **Flyway** - database migrations
- **Spring Data JPA** - database operations
- **Swagger/OpenAPI** - API documentation
- **Maven** - build system

## Requirements

- Docker and Docker Compose
- Or Java 17+ and Maven 3.8+ for local development

## Quick Start with Docker Compose

The easiest way to run the project is using Docker Compose:

```bash
# Clone the repository
git clone <repository-url>
cd opensource-reference-service

# Start services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f app
```

After startup, the service will be available at:
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html (available without authentication)
- **Swagger API Docs**: http://localhost:8080/v2/api-docs
- **Actuator**: http://localhost:8080/actuator
- **Prometheus Metrics**: http://localhost:8080/actuator/prometheus

> **Note**: Swagger UI is configured and available without authentication for convenient API development and testing.

PostgreSQL will be available on port 5432:
- **Host**: localhost
- **Port**: 5432
- **Database**: reference_service
- **Username**: postgres
- **Password**: postgres

## Security and Authentication

### Default User

On first installation, an administrator user is automatically created with the following credentials:
- **Login**: `admin`
- **Password**: `admin`

> **⚠️ IMPORTANT**: It is strongly recommended to **immediately change the administrator password** after the first installation of the service!

### Changing Administrator Password

After starting the service, execute the following request to change the administrator password:

```bash
# Get admin user ID (need to authenticate as admin)
curl -X GET http://localhost:8080/api/v1/users \
  -u admin:admin \
  -H "Content-Type: application/json"

# Change password (admin user ID is usually 1)
curl -X PATCH http://localhost:8080/api/v1/users/1/password \
  -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{"password": "new_secure_password"}'
```

Or use Swagger UI to perform these operations: http://localhost:8080/swagger-ui/index.html

### Basic Authentication

The service supports **HTTP Basic Authentication** for all protected endpoints (except Swagger UI and Actuator).

#### Using Basic Authentication

**In curl:**
```bash
curl -X GET http://localhost:8080/api/v1/product \
  -u username:password \
  -H "Content-Type: application/json"
```

**In HTTP requests:**
The `Authorization` header must contain `Basic <base64(login:password)>`:
```
Authorization: Basic YWRtaW46YWRtaW4=
```

Where `YWRtaW46YWRtaW4=` is the base64-encoded string `admin:admin`.

**Example header generation:**
```bash
# Linux/Mac
echo -n "admin:admin" | base64

# Or in Python
python3 -c "import base64; print(base64.b64encode(b'admin:admin').decode())"
```

**In Postman/Insomnia:**
1. Select authentication type "Basic Auth"
2. Enter login and password
3. The tool will automatically add the `Authorization` header

### User Management

#### Creating a New User

Only administrators can create new users:

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -u admin:your_password \
  -H "Content-Type: application/json" \
  -d '{
    "login": "new_user",
    "admin": false
  }'
```

**Parameters:**
- `login` - user login (Latin letters and numbers only, no spaces, up to 255 characters)
- `admin` - administrator flag (true/false)

**⚠️ Important**: When creating a user, the password is automatically set equal to the login. The new user **must change the password** after first login via the `PATCH /api/v1/users/{id}/password` endpoint.

#### Changing Own Password

Any user can change their own password:

```bash
curl -X PATCH http://localhost:8080/api/v1/users/{id}/password \
  -u your_login:your_current_password \
  -H "Content-Type: application/json" \
  -d '{"password": "new_password"}'
```

**Password Requirements:**
- Must not be empty
- Maximum 255 characters
- Must not contain spaces
- Must contain only ASCII characters (Latin letters, numbers, special characters)

### Access Rights

- **GET requests**: Available to all authenticated users
- **POST/PUT/PATCH requests**: Require administrator rights
- **Exception**: Users can change their own password via `PATCH /api/v1/users/{id}/password`

### Security Recommendations

1. **Change the administrator password** immediately after installation
2. **Use strong passwords** for all users
3. **Limit the number of administrators** - create administrators only when necessary
4. **Use HTTPS** in production environment
5. **Regularly review** the list of users and their access rights
6. **Do not use default passwords** in production

## Local Development

### Database Setup

1. Install PostgreSQL 15+
2. Create a database:
```sql
CREATE DATABASE reference_service;
```

3. Configure environment variables or create `application-local.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/reference_service
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Build and Run

```bash
# Build the project
mvn clean package

# Run the application
java -jar target/opensource-reference-service-0.0.11.jar
```

## API Endpoints

### Users
- `POST /api/v1/users` - Create user (requires administrator rights)
- `PATCH /api/v1/users/{id}/password` - Change user password (user can only change their own password)

> **Note**: All API endpoints (except Swagger UI and Actuator) require Basic authentication.

### Products
- `GET /api/v1/product` - Get all products
- `PUT /api/v1/product/{alias}` - Create/update product

### Business Capabilities
- `GET /api/v1/business-capability` - Get business capabilities (with pagination support)
- `PUT /api/v1/business-capability` - Create/update business capability

### Technical Capabilities
- `GET /api/v1/tech-capability` - Get technical capabilities (with pagination support)
- `PUT /api/v1/tech-capability` - Create/update technical capability

### System
- `GET /` - Application version information
- `GET /actuator/health` - Application health check
- `GET /actuator/metrics` - Application metrics
- `GET /actuator/prometheus` - Prometheus metrics

## Database Structure

The project uses Flyway for database migration management. Migrations are located in `src/main/resources/db/migration/`:

- `V0001__create_schema_users.sql` - Create users schema
- `V0002__create_table_user.sql` - Create user table
- `V0003__create_capability_schema.sql` - Create capability schema
- `V0004__create_business_capability_table.sql` - Create business capability table
- `V0005__create_products_schema_and_table.sql` - Create products schema and table
- `V0006__create_table_tech_capability.sql` - Create technical capability table
- `V0007__create_capability_tech_capability_relations_table.sql` - Create relations table
- `V008__add_column_to_product_table.sql` - Add columns to product table

## Configuration

Main application settings are in `application.properties`. For Docker environment, environment variables are used:

- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `APP_ROUTES_DASHBOARD` - Dashboard URL
- `APP_ROUTES_ARCHITECTURE_CENTER` - Architecture center URL
- `APP_ROUTES_STRUCTURIZR_BACKEND` - Structurizr backend URL

## Docker

### Building Image

```bash
docker build -t opensource-reference-service:latest .
```

### Running Container

```bash
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/reference_service \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  opensource-reference-service:latest
```

## Monitoring

The application provides metrics through Spring Boot Actuator and Prometheus:

- Health checks: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus endpoint: `/actuator/prometheus`

## Development

### Project Structure

```
src/main/java/ru/beeline/referenceservice/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── domain/          # JPA entities
├── dto/             # Data Transfer Objects
├── exception/       # Exceptions
├── filter/          # HTTP filters
├── mapper/          # Data transformation mappers
├── repository/      # JPA repositories
├── service/         # Business logic
└── util/            # Utilities
```

### Running Tests

```bash
mvn test
```

## License

See the [LICENSE.txt](LICENSE.txt) file for detailed license information.

## Copyright

Copyright (c) 2024 PJSC VimpelCom

## Contacts

For questions and suggestions, please create an issue in the project repository.
