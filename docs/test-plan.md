# Add2Num REST API Test Plan

## Test Conventions

Base URL:

```text
http://localhost:8080
```

API endpoint:

```text
POST /api/v1/add
```

Unless a test states otherwise, requests use:

```http
Content-Type: application/json
Accept: application/json
```

Successful API responses use `200 OK` and this JSON shape:

```json
{
  "result": "579"
}
```

Error responses use this stable shape. `requestId` is optional:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed.",
  "field": "a",
  "requestId": "opaque-request-id"
}
```

Assertions involving `requestId` must verify only that it is absent or opaque and non-sensitive; tests must not require a particular generated value unless request-ID behavior is made mandatory.

## 1. Normal Addition

**Given** a valid JSON request with `a = "123"` and `b = "456"`.

**When** the client sends `POST /api/v1/add`.

**Then** the service calculates the sum without using `BigInteger` in production code.

**Expected HTTP status:** `200 OK`

**Expected response body:**

```json
{
  "result": "579"
}
```

The response must not contain a `steps` property.

## 2. Different-Length Numbers

**Given** a valid JSON request:

```json
{
  "a": "1234",
  "b": "897"
}
```

**When** the client sends `POST /api/v1/add`.

**Then** the service aligns operands by their least significant digits and returns the correct sum.

**Expected HTTP status:** `200 OK`

**Expected response body:**

```json
{
  "result": "2131"
}
```

## 3. Carry Propagation

**Given** a valid JSON request:

```json
{
  "a": "999",
  "b": "1"
}
```

**When** the client sends `POST /api/v1/add`.

**Then** carries propagate across all required digit positions.

**Expected HTTP status:** `200 OK`

**Expected response body:**

```json
{
  "result": "1000"
}
```

A second case should verify a carry chain in both operand orders:

```json
{
  "a": "1",
  "b": "999999"
}
```

Expected body:

```json
{
  "result": "1000000"
}
```

## 4. Leading Zeros

**Given** a valid JSON request:

```json
{
  "a": "000123",
  "b": "000456"
}
```

**When** the client sends `POST /api/v1/add`.

**Then** leading zeros are accepted, counted toward the input length limit, and removed from the result.

**Expected HTTP status:** `200 OK`

**Expected response body:**

```json
{
  "result": "579"
}
```

## 5. Zero Values

**Given** each of the following valid requests:

```json
{
  "a": "0",
  "b": "0"
}
```

```json
{
  "a": "0000",
  "b": "0"
}
```

```json
{
  "a": "123",
  "b": "0"
}
```

**When** the client sends each request to `POST /api/v1/add`.

**Then** the service returns a canonical result and never an empty string.

**Expected HTTP status:** `200 OK` for every request.

**Expected response bodies:**

```json
{
  "result": "0"
}
```

```json
{
  "result": "0"
}
```

```json
{
  "result": "123"
}
```

## 6. Very Large Values

**Given** two valid decimal strings substantially larger than Java primitive types and no longer than `100,000` digits each. A deterministic fixture should be generated and its expected result calculated independently in the test oracle.

**When** the client sends the request to `POST /api/v1/add`.

**Then** the service returns the exact canonical decimal sum, completes with `O(n)` expected behavior, and does not use `BigInteger` in production calculation.

**Expected HTTP status:** `200 OK`

**Expected response body:**

```json
{
  "result": "<independently calculated canonical sum>"
}
```

The test must verify the result length and full value, not only a prefix or suffix. The response must not contain calculation steps.

## 7. Null and Blank Inputs

### Null `a`

**Given**:

```json
{
  "a": null,
  "b": "456"
}
```

**When** the client sends `POST /api/v1/add`.

**Then** validation stops before calculation.

**Expected HTTP status:** `400 Bad Request`

**Expected response body:**

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Field must not be null.",
  "field": "a"
}
```

An optional opaque `requestId` may also be present.

### Blank `a`

**Given**:

```json
{
  "a": "   ",
  "b": "456"
}
```

**When** the client sends `POST /api/v1/add`.

**Then** whitespace-only input is rejected and is not silently converted to zero.

**Expected HTTP status:** `400 Bad Request`

**Expected response body:**

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Field must not be blank.",
  "field": "a"
}
```

Repeat the null and blank cases with `b` and verify `field` is `b`.

### Empty string and missing fields

**Given** one of these requests:

```json
{
  "a": "",
  "b": "456"
}
```

```json
{
  "b": "456"
}
```

**When** the client sends `POST /api/v1/add`.

**Then** the invalid or missing operand is rejected.

**Expected HTTP status:** `400 Bad Request`

**Expected response body:**

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Field must not be blank."
}
```

For the missing-field case, `field` should identify the missing field as `a` or `b`.

## 8. Non-Digit Input

**Given** each invalid request contains a non-digit or formatted operand:

```json
{
  "a": "12x3",
  "b": "456"
}
```

```json
{
  "a": "1.0",
  "b": "456"
}
```

```json
{
  "a": "1,000",
  "b": "456"
}
```

```json
{
  "a": "１２３",
  "b": "456"
}
```

**When** the client sends each request to `POST /api/v1/add`.

**Then** only ASCII decimal digits are accepted and calculation does not start.

**Expected HTTP status:** `400 Bad Request`

**Expected response body:**

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Field must contain ASCII digits only.",
  "field": "a"
}
```

## 9. Negative Input

**Given**:

```json
{
  "a": "-1",
  "b": "456"
}
```

**When** the client sends `POST /api/v1/add`.

**Then** the negative operand is rejected because the API supports only non-negative integers.

**Expected HTTP status:** `400 Bad Request`

**Expected response body:**

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Field must be a non-negative integer.",
  "field": "a"
}
```

The response must not echo `-1`.

## 10. Input Exceeding the Configured Digit Limit

**Given** an operand containing `100,001` ASCII digits before normalization:

```json
{
  "a": "<100001 digits>",
  "b": "1"
}
```

**When** the client sends `POST /api/v1/add`.

**Then** validation rejects the request before calculation or allocation proportional to the full operation.

**Expected HTTP status:** `413 Payload Too Large` or `400 Bad Request`, according to the final implementation decision. The selected status must be used consistently.

**Expected response body:**

```json
{
  "code": "INPUT_TOO_LARGE",
  "message": "Input exceeds the maximum allowed length.",
  "field": "a"
}
```

An optional opaque `requestId` may also be present. The response must not contain the submitted digit string.

A separate test should verify that a `100,000`-digit operand is accepted if the request body remains within the configured body-size limit.

### Request body limit

**Given** a JSON request whose body exceeds `200,032` bytes, even though each individual operand is no longer than `100,000` digits.

**When** the client sends `POST /api/v1/add`.

**Then** the HTTP boundary rejects the request before JSON deserialization and before domain calculation.

**Expected HTTP status:** `413 Payload Too Large`

**Expected response body:**

```json
{
  "code": "INPUT_TOO_LARGE",
  "message": "Request body exceeds the maximum allowed size."
}
```

## 11. Malformed JSON

**Given** the request body is syntactically invalid JSON:

```text
{"a":"123","b":"456"
```

**When** the client sends `POST /api/v1/add` with `Content-Type: application/json`.

**Then** parsing fails before validation and calculation.

**Expected HTTP status:** `400 Bad Request`

**Expected response body:**

```json
{
  "code": "MALFORMED_JSON",
  "message": "Request body contains malformed JSON."
}
```

An optional opaque `requestId` may also be present. Parser details and the raw body must not be exposed.

## 12. Unsupported HTTP Methods

### GET

**Given** no request body.

**When** the client sends `GET /api/v1/add`.

**Then** the method is rejected because the API defines only `POST`.

**Expected HTTP status:** `405 Method Not Allowed`

**Expected response body:** Framework or application error representation. If the API error handler standardizes this response, it must use the stable error shape without exposing implementation details.

### PUT, DELETE, and PATCH

**Given** a request to `/api/v1/add` using `PUT`, `DELETE`, or `PATCH`.

**When** the client sends the request.

**Then** the method is rejected without performing a calculation.

**Expected HTTP status:** `405 Method Not Allowed`

**Expected response body:** Same policy as the GET case. **OPEN QUESTION:** The source specification does not define a stable error code for unsupported methods; decide whether these framework errors must use the common error schema.

## 13. Concurrent Requests

**Given** at least 20 concurrent valid requests with distinct operand pairs and expected results, for example:

```json
{
  "a": "999",
  "b": "1"
}
```

```json
{
  "a": "12345678901234567890",
  "b": "10"
}
```

```json
{
  "a": "000123",
  "b": "000456"
}
```

**When** all clients send `POST /api/v1/add` concurrently.

**Then** every response matches the request that produced it, no response contains another request's result or history, and no request fails because another request is running.

**Expected HTTP status:** `200 OK` for every valid request.

**Expected response bodies:** The response for each request must be exactly its independently calculated result, for example:

```json
{
  "result": "1000"
}
```

```json
{
  "result": "12345678901234567900"
}
```

```json
{
  "result": "579"
}
```

The test should repeat the batch several times and include requests with different operand lengths. It must verify that the REST path does not depend on shared mutable calculation-history state.

## 14. Existing HTML UI Regression

### Page load

**Given** the application is running.

**When** the client sends `GET /` and `GET /index`.

**Then** both routes return the existing Thymeleaf page.

**Expected HTTP status:** `200 OK`

**Expected response body:** HTML containing the existing addition form and no JSON API response body. The exact markup should be asserted only for stable form fields and page markers.

### HTML addition

**Given** the existing HTML form is submitted with form parameters `a=1234` and `b=897`.

**When** the client sends `POST /add` using the existing form encoding.

**Then** the request continues to render the Thymeleaf page with the result and calculation steps.

**Expected HTTP status:** `200 OK`

**Expected response body:** HTML containing:

- The submitted values `1234` and `897` as currently supported by the UI.
- The calculated result `2131`.
- At least one existing calculation-step entry.

The HTML regression test must also verify that adding the REST endpoint does not change `/add` into a JSON response.

## Cross-Cutting Assertions

Apply these assertions to applicable tests:

- Error bodies never contain raw operands, full results, stack traces, class names, or calculation-history text.
- Successful API bodies contain `result` and do not contain `steps`.
- API responses use JSON when a JSON response is defined.
- Leading/trailing whitespace is rejected rather than trimmed.
- Numeric JSON values are rejected because operands must be JSON strings.
- Production calculation does not call or depend on `BigInteger`.
- The API does not persist request or response data.
- Request IDs, when present, are opaque and contain no operand or result data.

## Test Execution Groups

1. Contract and controller tests: Sections 1-12.
2. Concurrency and thread-safety tests: Section 13.
3. Existing UI regression tests: Section 14.
4. Core regression tests: existing `core` unit tests, including large-number arithmetic.
5. Build verification: run the Maven test suite and package build after all groups pass.
