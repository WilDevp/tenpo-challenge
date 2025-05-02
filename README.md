# Tenpo Challenge REST API

## Overview

A Spring Boot (Java 21) REST API that:
- Adds two numbers and applies a dynamic percentage
- Uses Redis for distributed caching with fallback strategies
- Records operation history in PostgreSQL

## Quick Start

### Running from Docker Hub

This application is available as a Docker image on Docker Hub: `wildevp/tenpo-challenge`

```bash
# Pull the image directly (optional)
docker pull wildevp/tenpo-challenge

# 1. Clone this repository
git clone https://github.com/WilDevp/tenpo-challenge.git
cd tenpo-challenge

# 2. Start all services (app, PostgreSQL and Redis)
docker-compose up -d

# 3. Wait approximately 30 seconds for all services to initialize
# 4. Test the API
curl -X POST http://localhost:8080/api/v1/calculate \
  -H "Content-Type: application/json" \
  -d '{"num1": 10, "num2": 20}'
```

That's it! The API will be available at: http://localhost:8080/api

**Note:** The docker-compose.yml is already configured to use the pre-built Docker Hub image `wildevp/tenpo-challenge`.

### Alternative: Local Development

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
```

### Using External Database Clients

Connect to the PostgreSQL database using your preferred client (DBeaver, pgAdmin, etc.) with these settings:

```
Host: localhost
Port: 5432
Database: tenpo
Username: postgres
Password: password
```

## Redis Commands

```bash
# Connect to Redis CLI
docker exec -it tenpo-challenge-redis-1 redis-cli

# Check cached percentage
docker exec -it tenpo-challenge-redis-1 redis-cli GET "percentages::current"
```

## Architecture

The application uses:
- Hexagonal Architecture (domain, application, infrastructure layers)
- Strategy pattern for percentage retrieval
- Repository pattern for database access
