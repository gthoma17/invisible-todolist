# Invisible TodoList - Spring Boot Version

This is a Spring Boot conversion of the original Django-based Invisible TodoList application, written in Kotlin.

## Overview

A functional demo app for the Invisible Screen that displays random images from Lorem Picsum. The application handles JWT authentication, device installations, and screen orientation settings.

## Technology Stack

- **Spring Boot 3.2.0** - Main framework
- **Kotlin** - Programming language
- **Spring Data JPA** - Data persistence
- **Spring Security** - Security framework
- **Thymeleaf** - Template engine
- **PostgreSQL** - Production database
- **H2** - Test database
- **Gradle** - Build tool

## Features

- JWT-based authentication for device installations
- One-time login tokens for secure web access
- Screen orientation settings (vertical/horizontal)
- Dynamic image rendering based on device type and orientation
- Session management for settings page
- Comprehensive test suite with MockMvc and AssertJ

## API Endpoints

### Authentication
- `GET /api/get-login-token` - Get a one-time login token (requires JWT)
- `GET /api/login` - Login with one-time token and redirect to settings

### Settings
- `GET /settings` - View settings page (requires session)
- `POST /settings` - Update screen orientation (requires session)

### Rendering
- `GET /api/render` - Get rendered image for device (requires JWT)

## Supported Device Types

- `BLACK_AND_WHITE_SCREEN_880X528`
- `BLACK_AND_WHITE_SCREEN_800X480`

## Environment Variables

- `DATABASE_URL` - Database connection URL
- `B64_JWT_PUBLIC_KEY` - Base64-encoded JWT public key
- `MY_DEVELOPER_ID` - Developer ID for JWT validation
- `DJANGO_SECRET_KEY` - Application secret key

## Running the Application

### Prerequisites
- Java 17 or higher
- Gradle 7.0 or higher

### Development
```bash
./gradlew bootRun
```

### Testing
```bash
./gradlew test
```

### Building
```bash
./gradlew build
```

## Database Schema

### app_installation
- `installation_id` (UUID, Primary Key)
- `is_vertically_oriented` (Boolean)

### one_time_token
- `id` (Long, Primary Key)
- `installation_id` (UUID, Unique)
- `expiration_time` (LocalDateTime)
- `token` (String, 100 chars)

## Security

- JWT tokens are validated using RSA256 algorithm
- One-time tokens expire after 10 minutes
- Session-based authentication for settings page
- CSRF protection enabled for form submissions

## Testing

The application includes comprehensive tests using:
- **MockMvc** for web layer testing
- **AssertJ** for fluent assertions
- **Mockito** for mocking dependencies
- **Spring Boot Test** for integration testing

Test coverage includes:
- Controller layer tests
- Service layer tests
- Integration tests
- Security configuration tests

## Migration from Django

This Spring Boot version maintains API compatibility with the original Django application while providing:
- Better type safety with Kotlin
- More robust dependency injection
- Enhanced testing capabilities
- Improved performance and scalability