# URL Shortener Agentic Workflow Demo

This repository contains a small full-stack URL shortener prototype built as a local demo for an agentic software engineering workflow.

## Project structure

- `backend/` - Spring Boot REST API and persistence layer
- `frontend/` - React + Vite user interface
- `docs/` - project requirements, architecture, orchestration, and risk documentation
- `.github/` - repository automation and workflow metadata

## What the app does

- Accepts a long URL and creates a shortened code
- Stores the mapping in an in-memory H2 database
- Resolves the short code back to the original URL
- Tracks click analytics
- Exposes a browser-based interface for local testing

## Tech stack

- Java 17
- Spring Boot 3.3.4
- Spring Data JPA
- H2 in-memory database
- React + Vite

## Local development

### Backend

```bash
cd backend
mvn spring-boot:run
```

The backend runs on:
- http://localhost:9091

### Frontend

```bash
cd frontend
npm install
npm run dev -- --host 0.0.0.0
```

The frontend runs on:
- http://localhost:5173

## Main API endpoints

```http
POST http://localhost:9091/api/shorten
GET http://localhost:9091/api/analytics/{shortCode}
GET http://localhost:9091/{shortCode}
```

## Validation and testing

```bash
cd backend
mvn test
```

## Known notes for future work

- The backend stores data in H2 for local demo use only
- The frontend should use real external URLs and avoid self-referential localhost URLs
- The root orchestration concepts are intentionally lightweight and can be expanded into a more formal workflow engine

## Current status

This project is in a working local demo state with backend, frontend, validation, redirect behavior, and supporting documentation in place.

## Next improvements to consider

- richer workflow orchestration and governance model
- better observability and metrics
- stronger validation and test coverage
- release controls and human approval gates
