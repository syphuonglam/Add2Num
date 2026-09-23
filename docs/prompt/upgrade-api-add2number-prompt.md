Senior Software Developer with AI Native SDLC
Context: Upgrading the project "Add2Num" #sym:MyBigNumber by adding a submodule with Spring to provide API #sym:MyBigNumber
Task: Suggest me next steps to generate prompts to generate acceptable source code. For example, next step is to prepare prompts to generate a spec.md from the requirement but I am sure, pls give a guide.

Prompt 1: Establish the repository facts
You are analyzing an existing Maven multi-module Java project.

Repository facts:
- Java version: 21
- Modules: core and web
- core contains MyBigNumber, which adds arbitrarily long positive integer strings.
- web is a Spring Boot application using Thymeleaf.
- The current web controller supports an HTML form POST at /add.
- We want to add a JSON REST API without breaking the existing HTML UI.
- Follow docs/coding-rules.md.
- Do not modify source code yet.

Tasks:
1. Inspect the repository structure and relevant source files.
2. Identify existing behavior and public APIs.
3. Identify contradictions between the repository and coding rules.
4. List ambiguities that must be decided before implementation.
5. Recommend the smallest API design that fits the current project.

Return:
- Existing behavior
- Risks and ambiguities
- Decisions required
- Recommended next artifacts

Prompt 2: Generate spec.md
Implement the API tests described in docs/test-plan.md.

Constraints:
- Use the repository's existing test framework and style.
- Add focused controller/API tests and service/domain tests where appropriate.
- Do not modify production code to make tests pass.
- Run the narrowest relevant Maven test command after editing.
- Report failing tests and classify each failure as:
  1. expected because production code is not implemented yet,
  2. test defect,
  3. specification ambiguity.

Prompt 3: Ask for a contract before implementation
Using docs/spec.md, generate docs/api-contract.md.

Include:
- HTTP method and path
- Request and response JSON examples
- Success status
- Validation error status
- Malformed JSON behavior
- Error response schema
- Field-level validation messages
- Content-Type requirements
- Correlation/request ID behavior, if needed

Then generate an OpenAPI 3.0 YAML contract at docs/openapi.yaml.

Do not implement Java code.
Ensure the contract matches the specification exactly.

Prompt 4: Generate a test plan first
Using docs/spec.md and docs/openapi.yaml, create docs/test-plan.md.

Define tests for:
- Normal addition
- Different-length numbers
- Carry propagation
- Leading zeros
- Zero values
- Very large values
- Null and blank inputs
- Non-digit input
- Negative input
- Input exceeding the configured digit limit
- Malformed JSON
- Unsupported HTTP methods
- Concurrent requests
- Existing HTML UI regression

For every test, specify:
- Given
- When
- Then
- Expected HTTP status
- Expected response body

Prompt 5: Ask the AI to implement tests before production code:
Implement the API tests described in docs/test-plan.md.

Constraints:
- Use the repository's existing test framework and style.
- Add focused controller/API tests and service/domain tests where appropriate.
- Do not modify production code to make tests pass.
- Run the narrowest relevant Maven test command after editing.
- Report failing tests and classify each failure as:
  1. expected because production code is not implemented yet,
  2. test defect,
  3. specification ambiguity.

Prompt 6: Generate the implementation in vertical slices
DTOs and validation
Implement the request and response DTOs required by docs/spec.md and docs/openapi.yaml.

Requirements:
- Use explicit validation annotations where appropriate.
- Use constructor injection in Spring components.
- Keep DTOs separate from domain classes.
- Do not change MyBigNumber yet.
- Add focused tests for validation behavior.
- Follow docs/coding-rules.md.

Prompt 7: Generate the implementation in vertical slices
Application service
Implement the application service for the add-number use case.

Requirements:
- Depend on the existing MyBigNumber domain API.
- Keep the service stateless and thread-safe.
- Do not use BigInteger for production calculation.
- Do not log raw input values or full calculation history.
- Preserve the existing HTML workflow.
- Add unit tests for successful and invalid use cases.

Prompt 8: REST controller and exception handling
Implement POST /api/v1/add according to docs/openapi.yaml.

Requirements:
- Keep the existing HTML controller behavior unchanged.
- Use a dedicated REST controller.
- Use a stable JSON error response.
- Handle validation failures through centralized exception handling.
- Return no stack traces or raw input values to clients.
- Add MockMvc or equivalent API tests.
- Do not add undocumented endpoints.

Prompt 9: Ask for a review pass, after implementation
Review the implementation against:
- docs/spec.md
- docs/openapi.yaml
- docs/test-plan.md
- docs/coding-rules.md

Do not modify files yet.

Findings must be ordered by severity:
1. Functional defects
2. Security or denial-of-service risks
3. Thread-safety problems
4. Contract mismatches
5. Missing tests
6. Maintainability issues

For every finding include:
- File
- Symbol or approximate location
- Problem
- Concrete failure scenario
- Recommended fix

Prompt 10: Run a verification prompt
Validate the completed feature.

Run:
1. mvn -pl core test
2. mvn -pl web test
3. mvn clean package
4. Any API integration tests
5. Static analysis configured by the project

Then verify:
- Existing core tests still pass.
- Existing HTML behavior still works.
- POST /api/v1/add follows the OpenAPI contract.
- Invalid requests return the documented errors.
- Concurrent requests do not mix calculation histories.
- No production calculation uses BigInteger.

Return the exact commands run, results, and any remaining risks.

Prompt 11: Upgrade security / denial-of-service risks level is high
- No HTTP request-body limit
- API requests trigger sensitive calculation logging