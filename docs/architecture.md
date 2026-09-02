# Architecture Overview

## High-level design
This project follows a simple layered Spring Boot architecture:

1. Controller layer
   - Exposes REST endpoints
   - Converts HTTP requests into service calls

2. Service layer
   - Implements business logic
   - Validates URL input
   - Generates short codes
   - Tracks analytics updates

3. Repository layer
   - Persists URL mappings using Spring Data JPA
   - Performs lookups by short code

4. Persistence layer
   - H2 in-memory database for local prototype use

5. Exception handling layer
   - Converts unexpected runtime errors into structured HTTP responses

## Runtime flow
### Create short URL
- Client sends a POST request with a long URL
- Controller calls the service
- Service validates the URL format
- Service generates a unique short code
- Service saves the record
- Service returns the short code

### Redirect flow
- Client requests `/SHORTCODE`
- Controller resolves the short code
- Service loads the original URL
- Service increments the click count
- Controller issues a 302 redirect to the original URL

### Analytics flow
- Client requests `/api/analytics/{shortCode}`
- Service loads the stored entity
- Service returns the short code, target URL, click count, and creation timestamp

## Key design decisions
### Spring Boot + JPA
This keeps the prototype easy to run and easy to reason about. It is ideal for a small service with a single table and straightforward CRUD logic.

### H2 for local development
H2 provides fast local setup without requiring external infrastructure. This helps with demo and testing simplicity.

### Validation at the DTO boundary
Input validation happens early, with DTO annotations preventing invalid requests before they reach business logic.

### Single-entity model
The app stores a single `ShortUrl` entity with a mapping of short code → original URL. This keeps the architecture simple and exposes the core domain clearly.

## Current limitations
- It is not distributed or multi-instance aware
- It does not implement caching or rate limiting
- It is not production-hardened against abuse or malicious URLs
- It does not include a real agentic workflow engine
- It does not expose richer observability or analytics dashboards

## System boundaries
The system is intentionally small and focused. It has one clear domain and a single data store. That makes it excellent for a prototype and for demonstrating engineering fundamentals, while remaining honest about the broader assignment gaps.
