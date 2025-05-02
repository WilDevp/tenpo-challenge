# ADR 001: Architecture for Tenpo Challenge REST API

## Status
draft

## Date
2025-01-05

## Context
A requirement has been received to develop a REST API that allows calculations with dynamic percentages. The main functionalities required are:

1. Add two numbers and apply an additional percentage obtained from an external service
2. Implement a cache system to store the percentage and retrieve it when the external service is not available
3. Maintain a history of all operations performed in the API

The system requires high availability, ensuring that it functions even when the external percentage service is not available.

## Decisions

### 1. Hexagonal Architecture (Ports and Adapters)

We will implement a hexagonal architecture that clearly separate:

- **Domain**: Entities, value objects, and ports (interfaces)
- **Application**: Use cases and service implementations
- **Infrastructure**: Adapters for communication with external components (REST API, database, external services)

This architecture will allow us to:
- Keep the business domain isolated from technical details
- Facilitate changes in external components without affecting business logic
- Enable more direct and efficient unit testing

### 2. Technologies to Use

- **Spring Boot 3.2.0**: Base framework for the application
- **Java 21**: Language version
- **PostgreSQL**: Database management system for persistence
- **Redis**: Distributed cache management system (alternative to consider: Caffeine for simpler environments)
- **Docker/Docker Compose**: For containerization and simplified deployment
- **Gradle**: As dependency management and build system

### 3. Design Patterns

We will implement the following patterns to solve specific aspects:

- **Strategy**: To handle different ways of obtaining the percentage (external service, cache, default value)
- **Decorator**: To add cross-cutting functionalities such as logging and performance measurement
- **Repository**: To abstract database access
- **Factory**: For creating complex objects such as calculation results
- **Solid**: To define a good practices and clean code

### 4. Percentage Management and Cache

We will implement a cascading strategy for obtaining the percentage:

1. **External Service**: First priority, try to obtain the percentage from the external service
2. **Cache**: If the external service fails, look in the cache for the last valid percentage
3. **Default Value**: As a last resort, use a predefined value (10%)

For the cache:
- We will use Redis as a distributed cache system
- We will configure a TTL (Time-To-Live) of 30 minutes for cache entries
- In development/test environments, we will offer configuration to use in-memory cache

### 5. Operations Log (Audit Log)

For logging operations:

- An asynchronous process will be implemented to avoid affecting the performance of the main operations
- Complete information of each request will be stored: date, endpoint, parameters, response, status code
- Spring Data JPA will be used for persistence in PostgreSQL
- Pagination will be used to retrieve the history

### 6. REST API and Documentation

- RESTful endpoints with versioning (v1)
- Documentation with OpenAPI/Swagger
- Input validation with Spring Validation
- Centralized exception handling

### 7. Testing

- Unit tests for each component using JUnit 5 and Mockito
- Integration tests with TestContainers for real database and cache environments
- Specific profiles for testing that do not require external infrastructure

## Consequences

### Positive

- **Maintainability**: The hexagonal architecture will facilitate code maintenance and evolution
- **Testability**: Clear separation of responsibilities will allow more precise and complete tests
- **Scalability**: Using Redis as a distributed cache will allow horizontal scaling
- **Fault tolerance**: The cascading fallback strategy will ensure high availability

### Negative

- **Initial complexity**: A hexagonal architecture can be more complex initially
- **Infrastructure overhead**: Requires configuring and maintaining additional services (Redis, PostgreSQL)
- **Learning curve**: The team will need to become familiar with the chosen patterns and architecture

## Alternatives Considered

### Monolithic vs. Microservices Architecture

Given the current scope of the project, a monolithic architecture with good internal separation (hexagonal) offers the best balance between simplicity and scalability. Microservices would add unnecessary complexity at this stage.

### In-Memory vs. Distributed Cache

Although an in-memory cache like Caffeine is simpler to configure, we opted for Redis as a distributed cache solution to allow:
- Sharing the cache between multiple application instances
- Data persistence even after restarts
- Greater scalability in production environments

### Relational vs. NoSQL Database

For storing operation logs, a relational database like PostgreSQL offers:
- Data integrity and ACID transactions
- Complex and flexible queries using SQL
- Maturity and wide support in the Spring ecosystem

## Hexagonal Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│  Infrastructure Layer                                           │
│                                                                 │
│  ┌─────────────┐   ┌────────────┐   ┌───────────────────┐      │
│  │             │   │            │   │                   │      │
│  │ Controllers │   │ Repository │   │ External Services │      │
│  │             │   │            │   │                   │      │
│  └──────┬──────┘   └─────┬──────┘   └─────────┬─────────┘      │
│         │                │                    │                 │
└─────────┼────────────────┼────────────────────┼─────────────────┘
          │                │                    │                  
┌─────────┼────────────────┼────────────────────┼─────────────────┐
│         │                │                    │                 │
│  Application Layer       ▼                    ▼                 │
│                  ┌────────────────┐  ┌─────────────────┐       │
│                  │                │  │                 │       │
│                  │    Services    │  │   Strategies    │       │
│                  │                │  │                 │       │
│                  └────────┬───────┘  └────────┬────────┘       │
│                           │                   │                 │
│         ┌─────────────────┴───────────────────┘                 │
│         │                                                       │
└─────────┼───────────────────────────────────────────────────────┘
          │                                                        
┌─────────┼───────────────────────────────────────────────────────┐
│         ▼                                                       │
│  Domain Layer                                                   │
│                                                                 │
│  ┌─────────────┐   ┌────────────┐   ┌───────────────────┐      │
│  │             │   │            │   │                   │      │
│  │   Models    │   │    Ports   │   │    Exceptions     │      │
│  │             │   │            │   │                   │      │
│  └─────────────┘   └────────────┘   └───────────────────┘      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Percentage Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│                 StrategyBasedPercentageService                  │
│                                                                 │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│                 Chain of PercentageStrategy                     │
│                                                                 │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                  ┌─────────────┴──────────────┐
                  │                            │
                  ▼                            ▼
┌────────────────────────────┐    ┌───────────────────────────┐
│                            │    │                           │
│  ExternalServiceStrategy   │────▶       CacheStrategy       │────┐
│        (Primary)           │    │    (Redis - 30 min TTL)   │    │
│                            │    │                           │    │
└────────────────────────────┘    └───────────────────────────┘    │
                                                                   │
                                                                   │
                                   ┌───────────────────────────┐   │
                                   │                           │   │
                                   │  DefaultPercentageStrategy│◀──┘
                                   │     (Value = 10%)         │
                                   │                           │
                                   └───────────────────────────┘
```

## System Component Diagram

```
┌────────────────────────────────────┐              ┌────────────────────┐
│                                    │              │                    │
│             Spring Boot            │              │                    │
│             Application            │              │      Redis         │
│                                    │◀─────────────▶      Cache         │
│  ┌──────────────┐ ┌─────────────┐  │              │                    │
│  │              │ │             │  │              │                    │
│  │  API         │ │  Service    │  │              └────────────────────┘
│  │  Controllers │ │  Layer      │  │                        ▲           
│  │              │ │             │  │                        │           
│  └──────┬───────┘ └──────┬──────┘  │                        │           
│         │                │         │              ┌─────────┴──────────┐
│         ▼                ▼         │              │                    │
│  ┌──────────────┐ ┌─────────────┐  │              │     External       │
│  │              │ │             │  │              │     Percentage     │
│  │  Repositories│ │  Domain     │  │              │     Service        │
│  │              │ │  Layer      │  │              │                    │
│  └──────┬───────┘ └─────────────┘  │              └────────────────────┘
│         │                          │                                    
│         ▼                          │                                    
│  ┌──────────────┐                  │              ┌────────────────────┐
│  │              │                  │              │                    │
│  │  PostgreSQL  │◀─────────────────┼──────────────▶   User Browser     │
│  │  Database    │                  │              │                    │
│  │              │                  │              │                    │
│  └──────────────┘                  │              └────────────────────┘
│                                    │                                    
└────────────────────────────────────┘                                    
```

## References

- Spring Boot: https://spring.io/projects/spring-boot
- Hexagonal Architecture: https://netflixtechblog.com/ready-for-changes-with-hexagonal-architecture-b315ec967749
- Spring Data Redis: https://docs.spring.io/spring-data/redis/docs/current/reference/html/
- Design Patterns: https://refactoring.guru/design-patterns
