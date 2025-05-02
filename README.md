# Tenpo Challenge REST API

## Overview

A Spring Boot (Java 21) REST API that:
- Adds two numbers and applies a dynamic percentage
- Uses Redis for distributed caching with fallback strategies
- Records operation history in PostgreSQL

## Quick Start

### Docker (Recommended)

```bash
# Start all services (PostgreSQL, Redis, and API)
cd /path/to/project/tenpo-challenge
docker-compose up -d

# Check logs
docker-compose logs -f app
```

The API will be available at: http://localhost:8080/api

### Local Development

```bash
# Start PostgreSQL
docker run -d --name tenpo-postgres -p 5432:5432 -e POSTGRES_DB=tenpo -e POSTGRES_PASSWORD=postgres postgres:14

# Start Redis
docker run -d --name tenpo-redis -p 6379:6379 redis:7

# Run the application
./gradlew bootRun
```

## API Usage

### 1. Calculate with Percentage

**Endpoint:** `POST /api/v1/calculate`

**Example:**
```bash
curl -X POST http://localhost:8080/api/v1/calculate \
  -H "Content-Type: application/json" \
  -d '{"num1": 100.5, "num2": 200.75}'
```

**Response:**
```json
{
  "num1": 100.5,
  "num2": 200.75,
  "sum": 301.25,
  "percentage": 0.10,
  "result": 331.38
}
```

**Example Screenshot:**

![Calculate Endpoint](docs/resources/images/calculate.png)

### 2. Operation History

**Endpoint:** `GET /api/v1/history?page=0&size=10`

**Example:**
```bash
curl -X GET "http://localhost:8080/api/v1/history?page=0&size=10"
```

**Response:**
```json
{
  "content": [
    {
      "timestamp": "2025-05-02T12:45:30.123",
      "endpoint": "/v1/calculate",
      "requestParams": {
        "num1": 100.5,
        "num2": 200.75
      },
      "response": {
        "num1": 100.5,
        "num2": 200.75,
        "sum": 301.25,
        "percentage": 0.10,
        "result": 331.38
      },
      "statusCode": 200,
      "isError": false
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 42,
  "totalPages": 5
}
```

**Example Screenshot:**

![History Endpoint](docs/resources/images/history.png)

## Documentation

API documentation is available at:
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI: http://localhost:8080/api/docs

## Testing

```bash
# Run all tests
./gradlew test
```

## Database Access

### Using PostgreSQL CLI

```bash
# Connect to PostgreSQL container
docker exec -it tenpo-challenge-db-1 psql -U postgres -d tenpo

# View call history table
docker exec -it tenpo-challenge-db-1 psql -U postgres -d tenpo -c "SELECT * FROM call_records LIMIT 10;"

# Manually create database schema
# Note: This is normally done automatically by Flyway on application startup
docker cp src/main/resources/db/migration/V1__create_call_records_table.sql tenpo-challenge-db-1:/tmp/
docker exec -it tenpo-challenge-db-1 psql -U postgres -d tenpo -f /tmp/V1__create_call_records_table.sql
```

### Using External Database Clients

Connect to the PostgreSQL database using your preferred client (DBeaver, pgAdmin, etc.) with these settings:

```
Host: localhost
Port: 5432
Database: tenpo
Username: postgres
Password: postgres
```

**Example connection string:**
```
postgresql://postgres:postgres@localhost:5432/tenpo
```

## Redis Commands

```bash
# Connect to Redis CLI
docker exec -it tenpo-challenge-redis-1 redis-cli

# Check cached percentage
docker exec -it tenpo-challenge-redis-1 redis-cli GET "percentages::current"

# Monitor Redis operations in real-time
docker exec -it tenpo-challenge-redis-1 redis-cli MONITOR
```

### Using External Redis Clients

Connect to Redis using Redis Desktop Manager or another client with these settings:

```
Host: localhost
Port: 6379
No authentication required
```

## Configuration

Key configuration properties (in `application.yml`):
- Cache TTL: 30 minutes
- Default percentage fallback: 10%
- Async task pool size: 5-10 threads

## Database Migration

The application uses Flyway for database migrations:

```bash
# Schema creation is handled automatically on startup, but can be run manually:
docker exec -it tenpo-challenge-db-1 psql -U postgres -d tenpo -c "
CREATE TABLE IF NOT EXISTS call_records (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    endpoint VARCHAR(100) NOT NULL,
    request_params VARCHAR(1000),
    response VARCHAR(1000),
    error_message VARCHAR(1000),
    status_code INTEGER
);

CREATE INDEX idx_call_records_timestamp ON call_records (timestamp DESC);
"
```

## Architecture

The application uses:
- Hexagonal Architecture (domain, application, infrastructure layers)
- Strategy pattern for percentage retrieval
- Repository pattern for database access
