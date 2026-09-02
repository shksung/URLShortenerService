# Scenario Walkthroughs

## 1. Greenfield scenario
A new product requirement arrives: create a URL shortener that accepts a long URL and returns a small redirectable code.

### How the project addresses it
- Domain model is defined as a `ShortUrl` record
- REST endpoints expose creation and resolution behavior
- Repository stores mappings in H2
- Service logic manages validation and short-code generation

### Engineering reasoning
This is the simplest path because requirements are clear and the system boundary is narrow.

## 2. Brownfield scenario
A team already has a URL service and needs to add analytics or fix in-flight issues such as invalid URL handling, click tracking, or resilience around duplicate short codes.

### How the project addresses it
The design remains modular at service, repository, and controller boundaries. Any change in behavior can be localized to:

- validation rules
- repository queries
- analytics logic
- redirect logic

### Engineering reasoning
This demonstrates the value of clear boundaries and a single domain entity. Brownfield work is less about rewriting and more about evolving existing contracts safely.

## 3. Ambiguous scenario
A stakeholder says: “Make the short links easy to use and secure enough for real production traffic.”

### How the project resolves ambiguity
The team narrows this into concrete engineering questions:

- What is the expected URL format?
- Are duplicate URLs allowed to share the same short code?
- How should failures be surfaced to clients?
- What is the acceptable behavior if a short code does not exist?
- What metrics matter most for reliability?

### Engineering reasoning
This demonstrates requirement normalization: the broad ask is converted into specific design constraints, API contract decisions, and risk controls.

## Why these scenarios matter
They show that the project is not just a toy app. It is structured to demonstrate system thinking across different requirement shapes and engineering maturity levels.
