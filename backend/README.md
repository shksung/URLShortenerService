# URL Shortener Backend

This project is a Spring Boot prototype for the agentic software engineering assignment. It demonstrates a working URL shortener service, a lightweight architecture, and a structured way to frame the assignment requirements as an engineering problem.

## What the system does
- Creates a shortened URL from a valid long URL
- Resolves the short code back to the original target
- Tracks click analytics for each shortened URL
- Stores mappings in a local H2 database
- Validates input and returns consistent HTTP errors
- Exposes health and metrics endpoints via Spring Actuator

## Architecture summary
The application follows a simple layered design:

- Controller layer: REST API exposure
- Service layer: validation, URL normalization, short-code generation, analytics updates
- Repository layer: data access via Spring Data JPA
- Persistence layer: H2 in-memory database for local prototype use
- Exception handling: standardized error responses for bad input and runtime failures

## Requirement interpretation
The assignment was normalized into a concrete engineering problem:

- accept a long URL
- validate it
- generate a unique short code
- store the mapping
- resolve and track redirection
- expose the service over HTTP
- document the workflow and governance model behind the assignment

## Agentic workflow concept
The project includes a lightweight orchestration concept with the stages below:

1. requirements
2. architecture
3. implementation
4. validation
5. release-readiness

These stages represent the intended governance structure for a larger agentic SDLC flow, even though the current implementation is a simplified prototype rather than a full production orchestration engine.

## Scenario coverage
This project is structured to support the assignment’s required scenarios:

- Greenfield: build the shortener from scratch
- Brownfield: evolve service logic and data flows without rewriting the app
- Ambiguous: resolve vague requirements into concrete engineering decisions

Additional scenario write-ups are in the docs folder.

## Documentation index
- [docs/requirements.md](../docs/requirements.md)
- [docs/architecture.md](../docs/architecture.md)
- [docs/orchestration.md](../docs/orchestration.md)
- [docs/scenarios.md](../docs/scenarios.md)
- [docs/risk-assessment.md](../docs/risk-assessment.md)

## Run locally
```bash
cd backend
mvn spring-boot:run
```

## Example API calls
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/very/long/path"}'

curl http://localhost:8080/api/analytics/<shortCode>
curl -I http://localhost:8080/<shortCode>
```

## Validation notes
The project includes a basic Spring Boot test to validate startup and service behavior. More exhaustive validation would include controller tests, edge-case tests, and failure-mode testing.

## Resume notes for tomorrow
This project is already in a working local demo state. Before continuing, confirm the environment is still healthy:

1. Backend must run on port 9091
2. Frontend must run on port 5173
3. Browser should open to http://localhost:5173
4. Use a real external URL in the form
5. Do not shorten localhost or app-local URLs because they create redirect loops

Common pitfalls already solved:
- stale browser state after config changes
- port conflict from 8080/9090 usage
- redirect route mismatch
- self-referential URL acceptance causing broken redirect flows
- CORS mismatch between frontend and backend

## Current limitations
- lightweight orchestration concept, not a full execution engine
- H2 is suitable for local prototype use but not production deployment
- random short-code generation is simple but not human-friendly
- deeper observability, retry, rollback, and approval workflows are still future work

## Summary
This project is a credible MVP for the assignment: it has a working URL shortener, a clear layered architecture, validation, analytics, and documentation that explains how it fits the larger agentic engineering brief.

