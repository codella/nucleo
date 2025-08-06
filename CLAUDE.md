# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Nucleo is a Java framework that combines Eclipse Vert.x, RESTEasy, Weld CDI, and various SmallRye microservices libraries. It provides a structured way to build reactive, microservice-oriented applications with dependency injection, fault tolerance, health checks, and HTTP endpoints.

## Development Commands

### Build and Test
- `./gradlew build` - Build the entire project (both nucleo library and demo)
- `./gradlew test` - Run all tests
- `./gradlew :nucleo:test` - Run tests for the nucleo library only
- `./gradlew :demo:test` - Run tests for the demo application only
- `./gradlew clean` - Clean build artifacts

### Running the Demo
- `./gradlew run` - Run the demo application (starts on port 8080 for HTTP, 3030 for health)
- Demo exposes endpoints at `/resteasy`, `/sql-client`, and `/resteasy-client`
- Health endpoints available at `:3030/liveness` and `:3030/readiness`

### Other Tasks
- `./gradlew jar` - Create JAR files
- `./gradlew javadoc` - Generate API documentation
- `./gradlew tasks` - List all available tasks

## Architecture

### Core Framework Structure (nucleo/)
The main framework is built around several key components:

1. **Nucleo.java** - Main bootstrap class that orchestrates the entire framework
   - Uses Weld CDI container for dependency injection
   - Manages Vert.x instance lifecycle
   - Deploys and manages Verticles (async components)
   - Integrates SmallRye extensions (Config, Fault Tolerance, Health)

2. **Verticles** - Asynchronous components deployed on the Vert.x event loop:
   - `HealthVerticle` - Provides health check endpoints (liveness/readiness)
   - `ResteasyHttpVerticle` - HTTP server that integrates RESTEasy with Vert.x

3. **CDI Extensions**:
   - `VertxExtension` - Makes Vert.x instance available for CDI injection
   - `ResteasyExtension` - Auto-discovers JAX-RS resources and REST clients
   - Automatically configures beans with proper scopes

### Demo Application Structure (demo/)
Demonstrates framework usage with:
- `Demo.java` - Main class showing Nucleo bootstrap
- `DemoResteasyResource.java` - JAX-RS resource with database and client integration
- `DemoMysqlClient.java` - Database client configuration
- `DemoResteasyClient.java` - REST client interface

### Key Integration Points
- **Configuration**: Uses SmallRye Config with `@ConfigProperty` annotations
- **Database**: Vert.x reactive SQL clients (MySQL example in demo)
- **REST Clients**: MicroProfile REST Client with `@RegisterRestClient`
- **Health**: SmallRye Health with async support
- **Fault Tolerance**: SmallRye Fault Tolerance integration
- **Logging**: Google Flogger with SLF4J backend

### Configuration
- Main HTTP server port: `nucleo.http-server.port` (default: 8080)
- Health server port: `nucleo.health-server.port` (default: 3030)
- Application configuration in `src/main/resources/application.properties`

### Testing
- Uses JUnit 5 with Vert.x JUnit integration
- Integration tests verify CDI extensions and Verticle deployments
- Test configuration in `src/test/resources/application.properties`

## Development Patterns

### Creating New Resources
1. Annotate with `@Path` for auto-discovery by ResteasyExtension
2. Use `@Inject` for dependency injection of configured beans
3. Return `CompletionStage<T>` for async operations with Vert.x

### Adding New Verticles
1. Extend `AbstractVerticle`
2. Use CDI `@Inject` in constructor for dependencies
3. Register with Nucleo using `withVerticleBeanClass()`

### Configuration Properties
Use `@ConfigProperty` with SmallRye Config - automatically discovers properties from application.properties and environment variables.

### Database Integration
Use Vert.x reactive SQL clients, return `CompletionStage` for async operations that integrate with Vert.x event loop.