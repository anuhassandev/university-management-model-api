# School Management API

A robust and scalable RESTful API for managing a school’s timetable, including details for convenors, modules, and sessions. This project demonstrates key skills in backend development using Spring Boot, while adhering to modern API design principles.

---

## Features

- **Convenor Management**: Add, update, delete, and retrieve convenors.  
- **Module Management**: Manage school modules, including creating, updating, and listing.  
- **Session Management**: Schedule and organise sessions for modules with flexibility to filter by module or convenor.  
- **Validation**: Ensures data integrity with detailed validation for inputs like module codes, session timings, and more.  
- **Scalability**: Designed to handle growing data sets with ease.  
- **Documentation**: Fully documented API with Swagger OpenAPI 3.0 for ease of use and integration.

---

## Technologies Used

- **Java (Spring Boot)**: Core framework for the application.
- **Maven**: Build and dependency management.
- **MySQL**: Backend database.
- **Postman**: API testing and validation.
- **Swagger OpenAPI 3.0**: API documentation.

---

## API Endpoints

The API offers the following capabilities:

### Convenor Management
- **GET** `/convenors`: List all convenors.
- **POST** `/convenors`: Add a new convenor.
- **GET** `/convenors/{id}`: Retrieve a specific convenor by ID.
- **PUT** `/convenors/{id}`: Update a specific convenor.
- **DELETE** `/convenors/{id}`: Delete a convenor.
- **GET** `/convenors/{id}/modules`: List all modules taught by a specific convenor.

### Module Management
- **GET** `/modules`: List all modules.
- **POST** `/modules`: Add a new module.
- **GET** `/modules/{code}`: Retrieve a specific module by code.
- **PATCH** `/modules/{code}`: Update a specific module.
- **DELETE** `/modules/{code}`: Delete a module.
- **GET** `/modules/{code}/sessions`: List all sessions for a specific module.

### Session Management
- **GET** `/sessions`: List all sessions with optional filters for convenor ID and module code.
- **DELETE** `/sessions`: Delete all sessions.
- **GET** `/modules/{code}/sessions/{id}`: Retrieve a session by ID within a module.
- **POST** `/modules/{code}/sessions`: Add a session to a specific module.
- **PUT/PATCH** `/modules/{code}/sessions/{id}`: Update a session within a module.
- **DELETE** `/modules/{code}/sessions/{id}`: Delete a session within a module.

---

### Database Initialisation
The application initialises with this seed data for testing:
- 2 Convenors
- 2 Modules
- 2 Sessions

---

## Project Structure

### Packages
- `com.school.management.domain`: Contains the domain models (e.g., `Convenor`, `Module`, `Session`).
- `com.school.management.controller`: Contains the REST controllers (e.g., `ConvenorController`, `ModuleController`).
- `com.school.management.repo`: Contains the Spring Data JPA repositories.
- `com.school.management.validation`: Custom validators for domain models.

### Notable Files
- **`SchoolManagementApplication.java`**: The entry point of the application.
- **`application.properties`**: Configuration file for database and application settings.
- **`part1.yaml`**: Swagger API documentation.

---

## Validation Rules

### Convenors
- **ID**: Must be unique.
- **Name**: Cannot be empty.
- **Position**: Must be one of `GTA`, `Lecturer`, or `Professor`.

### Modules
- **Code**: Must be unique.
- **Title**: Cannot be empty.
- **Level**: Must be between 1 and 4.
- **Optional**: Indicates if the module is optional (boolean).

### Sessions
- **Datetime**: Must be a valid timestamp.
- **Duration**: Must be greater than 0.
- **Topic**: Cannot be empty.

---

## Example Project Highlights

### Example 1: Module Creation
```json
POST /modules
{
  "code": "SW_ARCH",
  "title": "Software Architecture",
  "level": 2,
  "optional": false,
  "sessions": []
}
```

### Example 2: Session Listing with Filters
```json
GET /sessions?convenor=1&module=SW_ARCH
```
