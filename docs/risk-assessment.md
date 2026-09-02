# Risks, Trade-offs, and Validation

## Key risks
### 1. Short code collisions
If the code generation logic is naive, duplicates could occur.

Mitigation:
- loop until a unique code is found
- persist uniqueness constraint at the database layer

### 2. Invalid or malicious URLs
The service must reject malformed URLs and avoid unsafe redirects.

Mitigation:
- enforce `http://` or `https://`
- validate before saving
- separate input validation from redirect execution

### 3. Abuse and spam traffic
A URL shortener is vulnerable to overuse and link spam.

Mitigation:
- rate limiting
- usage quotas
- abuse detection
- operational monitoring

### 4. Analytics accuracy
Click tracking can be imprecise if there are race conditions or multiple concurrent requests.

Mitigation:
- use transactional updates where appropriate
- keep database writes consistent
- monitor for unexpected increments

## Trade-offs
### Simplicity vs. production readiness
This project intentionally favors a small, understandable design over a distributed system.

### Local H2 vs. external database
H2 is excellent for demo and testability but not a realistic production data store for multi-node deployments.

### Random short codes vs. human-readable codes
Random codes are easy to generate and unique, but they are less memorable than structured short codes.

## Validation strategy
The current project validates the implementation through:

- unit-style service testing
- Spring Boot startup validation
- response contract checks
- input validation checks
- manual API smoke tests

## Safety guardrails
- reject invalid input early
- keep business logic in the service layer
- avoid broad exception swallowing
- maintain explicit system boundaries between API, service, and data access

## Overall assessment
This project is a credible MVP for the interview assignment, but it is intentionally honest about its limits. The strongest value is the project’s clarity, modularity, and ease of understanding. The main missing work is the deeper governance and operations layer expected by the assignment.
