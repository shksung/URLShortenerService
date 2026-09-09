# Resume Plan

## Project
URL Shortener Agentic Engineering Prototype

## Current status
The project is now in a strong MVP state and is working end-to-end locally:

- Spring Boot backend is running successfully on port 9091
- React frontend is running successfully on port 5173
- URL creation, redirect, and analytics flows are implemented and working
- JPA + H2 persistence are active for local demo/testing
- CORS has been configured for the frontend origin
- Basic documentation and architecture notes have been added
- Frontend can create short URLs through the browser UI
- Existing backend tests pass, and the browser interaction has been verified live

## Verified working setup
Use this exact setup when resuming work:

- Backend: http://localhost:9091
- Frontend: http://localhost:5173
- API endpoints:
  - POST http://localhost:9091/api/shorten
  - GET http://localhost:9091/api/analytics/{shortCode}
  - GET http://localhost:9091/{shortCode}

## Known issue / debugging note
The main issue earlier was not a backend logic error. It was a stale browser session combined with old port conflicts:

- frontend was trying to hit port 9090 before the port was corrected
- backend was not consistently available on the expected port
- browser and dev server caches kept stale page state
- the app needed a refresh or full browser reopen after the fix

If the browser still shows a failed fetch, do this first before changing code:

1. Check the backend is up on 9091
2. Check the frontend is open on 5173
3. Close and reopen the browser tab or reload with hard refresh
4. Confirm the page is not still pointing to an old frontend version

## Already completed
- Backend project scaffolding
- URL shortening service implementation
- Short code generation and uniqueness checks
- Analytics tracking for click counts
- Redirect endpoint behavior
- Validation and exception handling
- Basic README and documentation set
- Requirement/architecture/orchestration scenario write-ups
- React frontend scaffold and UI flows
- CORS fix for localhost:5173
- Port cleanup and correct local configuration

## What remains to make it stronger for the assignment
1. Add deeper orchestration logic
   - explicit dependency graph
   - stage gating
   - approval checkpoints
   - retry/fallback behavior
   - rollback and safe-stop concepts

2. Expand tests
   - invalid URL tests
   - short-code not found tests
   - analytics validation tests
   - controller integration tests
   - edge-case scenarios

3. Strengthen production realism
   - safer URL normalization
   - duplicate mapping handling
   - rate limiting or abuse prevention
   - observability metrics for workflow and service health

4. Finalize assignment narrative
   - clear greenfield, brownfield, and ambiguous scenario write-up
   - explicit architecture rationale
   - risk/assumption/limitation section

5. Add project polish for presentation
   - better frontend UX and validation messages
   - improved analytics display and link actions
   - maybe add a cleaner landing page or card-based layout

## Suggested next tasks
### Priority 1
- implement a workflow state service
- define stage dependency and gate logic
- add a human approval checkpoint concept before release-readiness

### Priority 2
- add controller and service tests for failure paths
- verify analytics and redirect behavior under multiple conditions

### Priority 3
- improve documentation narrative for the final interview submission
- add final summary with assumptions, risks, and trade-offs

### Priority 4
- polish the frontend experience and make the demo more presentation-ready
- add basic validation/error states to UI for user clarity

## Run commands
From the project root:

```bash
cd backend
mvn spring-boot:run
```

In a second terminal:

```bash
cd frontend
npm run dev -- --host 0.0.0.0
```

Run tests:

```bash
cd backend
mvn test
```

## Useful files to review
- backend/src/main/java/com/swhwab/urlshortener/service/UrlShortenerService.java
- backend/src/main/java/com/swhwab/urlshortener/controller/UrlShortenerController.java
- backend/src/main/java/com/swhwab/urlshortener/config/AgenticWorkflowConfig.java
- backend/src/main/resources/application.properties
- frontend/src/App.jsx
- frontend/src/App.css
- docs/requirements.md
- docs/architecture.md
- docs/orchestration.md
- docs/scenarios.md
- docs/risk-assessment.md

## Recommended resume workflow
When returning to work, start here:

1. Start backend on port 9091
2. Start frontend on port 5173
3. Open the browser at http://localhost:5173
4. Create a short URL and verify the API call succeeds
5. Then work on the next enhancement (tests, orchestration logic, or presentation polish)

This keeps the session grounded in a working demo and avoids wasting time on unrelated debugging.

## Resume checklist for tomorrow
Use this checklist before making any new changes:

### 1. Confirm the environment is clean
- Make sure no stale Java processes are still holding old ports
- If a port conflict appears, stop old Java processes before restarting the backend
- Check that the backend is listening on 9091 and not 8080 or 9090
- Hard-refresh or reopen the browser after changes to the frontend or backend config

### 2. Start the app in the right order
Backend:
cd backend
mvn spring-boot:run

Frontend:
cd frontend
npm run dev -- --host 0.0.0.0

### 3. Use the working URLs
- Frontend: http://localhost:5173
- Backend API: http://localhost:9091
- Example shorten request: POST http://localhost:9091/api/shorten
- Analytics request: GET http://localhost:9091/api/analytics/{shortCode}
- Redirect request: GET http://localhost:9091/{shortCode}

### 4. Validate the happy path
- Submit an external URL such as https://example.com/very/long/path
- Confirm a short code is returned
- Open the redirect link
- Fetch analytics and confirm click count increases

### 5. Guardrails to remember
- Do not shorten localhost URLs or app-local URLs because they create redirect loops
- Do not use app URLs that resolve back to the same local service
- Frontend validation blocks values with localhost:9091, 127.0.0.1:9091, and 0.0.0.0:9091
- Use real external targets when testing the demo

### 6. Known issues and fixes already completed
- Old port mismatch between frontend and backend caused failed fetches
- Browser session staleness made it look like the app had not updated
- Redirect route mismatch was fixed by using the proper root redirect endpoint
- Self-referential input was rejected to avoid broken redirect behavior
- CORS was configured for the frontend origin

## Final resume notes for tomorrow
The app is in a working local demo state. The biggest remaining assignment gap is not the URL shortener itself; it is the orchestration layer required by the project brief.

### Current honest status
- URL shortening, redirecting, analytics, and frontend flow are working
- The app is stable on local ports 9091 and 5173
- The project now has a minimal workflow model, but it is still lightweight rather than a full governance engine
- The next goal is to strengthen the orchestration story with explicit dependency checks, approval gates, and traceability

### Highest-priority tasks for tomorrow
1. Formalize the workflow dependency graph
2. Add API endpoints or service methods to manage workflow state
3. Improve approval gating for release-readiness
4. Add richer retry, rollback, and audit tracking
5. Document the orchestration model clearly in the final narrative

### Keep in mind during the next session
- Do not lose time on environment issue triage unless the app fails to start
- Reuse the validated backend port and frontend port rather than changing them again
- Validate the happy path before adding complexity
- Treat orchestration as the main differentiation layer for the assignment

## Session handoff summary
This project is no longer a purely backend-only prototype. It is now a frontend-backed demo app with a working API flow, clean local startup configuration, and a basic workflow orchestration model. The next session should focus on turning that workflow model into a more explicit and defensible governance layer while keeping the working demo intact.
