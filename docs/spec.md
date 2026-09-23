# Add2Num REST API Specification

## 1. Scope and Non-Goals

### Scope

Add a versioned JSON REST API to the existing Spring Boot `web` module for adding two arbitrarily long, non-negative integer strings.

The API must:

- Accept two decimal integer strings in a JSON request.
- Return their normalized sum as a JSON response.
- Preserve the existing Thymeleaf web UI and its routes.
- Use the existing `core` domain functionality for production calculation.
- Support inputs larger than Java primitive numeric types and without converting them to `BigInteger`.
- Be stateless and safe for concurrent requests.

### Non-goals

This feature does not include:

- Replacing the existing Thymeleaf UI.
- Changing the existing HTML form contract.
- Supporting subtraction, multiplication, division, fractions, or signed values.
- Persisting requests, responses, or calculation history.
- Authentication, authorization, rate limiting, or user accounts.
- Streaming responses.
- A client SDK.
- A change to the core public API unless required to make the implementation thread-safe and compatible.

## 2. API Endpoint and JSON Schemas

### Endpoint

```text
POST /api/v1/add
Content-Type: application/json
Accept: application/json
```

The endpoint is versioned so future incompatible changes can be introduced under a new version.

### Request schema

```json
{
  "a": "123",
  "b": "456"
}
```

Required fields:

- `a`: decimal digits representing a non-negative integer.
- `b`: decimal digits representing a non-negative integer.

Unknown JSON properties should be rejected or ignored consistently. **OPEN QUESTION:** Choose whether unknown properties are rejected (`400 Bad Request`) or ignored for forward compatibility.

### Success response

Status: `200 OK`

```json
{
  "result": "579"
}
```

The `result` value must be a canonical decimal string:

- It contains only ASCII digits.
- Leading zeros are removed.
- The value zero is represented as `"0"`.

Calculation steps are not included in the REST response. They remain available to the existing Thymeleaf UI through the existing domain/UI flow. This keeps the API response small and avoids exposing operand-derived calculation details in logs or external contracts.

### Content negotiation

A request with an unsupported content type should return `415 Unsupported Media Type`.

A request that does not accept JSON should return `406 Not Acceptable` when content negotiation cannot produce an acceptable representation.

## 3. Validation Rules

Validation occurs before calculation.

| Condition | Required behavior |
| --- | --- |
| Missing `a` or `b` | Return `400 Bad Request`. |
| JSON `null` for `a` or `b` | Return `400 Bad Request`. |
| Empty string | Return `400 Bad Request`. |
| Whitespace-only string | Return `400 Bad Request`. |
| Leading or trailing whitespace | Return `400 Bad Request`; do not silently trim API values. |
| Non-ASCII digit or non-digit character | Return `400 Bad Request`. |
| Negative value, such as `-1` | Return `400 Bad Request`. |
| Decimal or formatted value, such as `1.0`, `1,000`, or `+1` | Return `400 Bad Request`. |
| Leading zeros, such as `000123` | Accept and normalize to `123`. |
| All-zero value, such as `0000` | Accept and normalize to `0`. |
| Numeric JSON values instead of strings | Return `400 Bad Request`; the contract requires strings. |
| Input exceeding the configured digit limit | Return `413 Payload Too Large` or `400 Bad Request`, consistently. **OPEN QUESTION:** Select the final status code. |

The API accepts only the characters `0` through `9` in each operand. It does not accept signs, decimal points, separators, exponent notation, or Unicode digit characters.

The maximum operand length is defined in the performance section. Length is measured before leading-zero normalization to prevent bypassing the input limit with padded values.

## 4. Error Model

All client-visible errors must use a stable JSON shape:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed.",
  "field": "a",
  "requestId": "optional-request-id"
}
```

Fields:

- `code`: stable machine-readable error code.
- `message`: safe, human-readable message that does not include the submitted operands.
- `field`: optional field name associated with the error.
- `requestId`: optional identifier for support and log correlation.

Required error mappings:

| Situation | HTTP status | Error code |
| --- | ---: | --- |
| Malformed JSON | `400` | `MALFORMED_JSON` |
| Missing, null, blank, or invalid operand | `400` | `VALIDATION_ERROR` |
| Unsupported content type | `415` | `UNSUPPORTED_MEDIA_TYPE` |
| Unacceptable response format | `406` | `NOT_ACCEPTABLE` |
| Input exceeds configured limit | `413` or `400` | `INPUT_TOO_LARGE` |
| Unexpected server failure | `500` | `INTERNAL_ERROR` |

The API must not expose stack traces, class names, internal exception messages, raw operands, or calculation-history text in error responses.

Unexpected server failures must be logged with the request ID when available. Logs must not contain raw input values, full results, or calculation steps.

## 5. Thread-Safety Requirements

The REST request path must be stateless:

- No request data may be stored in mutable singleton fields.
- Controllers and services must be safe for concurrent use.
- Each request must receive an independent result object.
- Calculation history must not be shared between requests.
- One request must not be able to overwrite or clear another request's result or history.

The implementation must not rely on `getLastSteps()` from a shared `MyBigNumber` instance for the REST response.

If the current core implementation stores history in instance state, it must be isolated or refactored before using that path concurrently. Any such change must preserve the existing `sum(String, String): String` behavior and existing UI behavior.

## 6. Performance Limits

Production calculation must use digit-by-digit string arithmetic or equivalent logic already provided by the core module. It must not use `BigInteger` for calculation.

Default limits:

- Maximum operand length: `100,000` decimal digits per operand.
- Maximum request body size: `200,032` bytes, allowing two maximum-length operands and JSON overhead.
- Expected time complexity: `O(n)`, where `n` is the length of the larger operand.
- Expected memory complexity: `O(n)`.

The service must validate input length before allocating structures proportional to the full calculation size where practical.

Requests over either configured limit must fail at the HTTP boundary or before calculation. The request-body limit returns `413 Payload Too Large` with `INPUT_TOO_LARGE`. **OPEN QUESTION:** Confirm whether `100,000` digits is appropriate for the deployment environment and whether operand-level oversized input should remain `413` or become `400`.

The REST API must use a result-only domain operation that does not generate calculation history. It must not log operand contents, full results, or per-digit calculation steps, including at debug level in production configuration.

## 7. Acceptance Criteria

1. `POST /api/v1/add` accepts the documented JSON request.
2. `{"a":"123","b":"456"}` returns `200 OK` and `{"result":"579"}`.
3. The result is correct for operands larger than Java primitive numeric types.
4. Leading-zero operands are accepted and the result is canonicalized.
5. Zero is returned as `"0"`, never as an empty string.
6. Null, missing, blank, signed, decimal, formatted, and non-digit operands are rejected according to the validation rules.
7. Oversized operands are rejected before calculation.
8. Error responses use the documented stable JSON shape and codes.
9. Error responses do not reveal raw operands, stack traces, or internal implementation details.
10. The REST endpoint does not return calculation steps.
11. Concurrent requests cannot mix results or calculation history.
12. The existing `GET /`, `GET /index`, and HTML `POST /add` behavior continues to work.
13. Existing core tests continue to pass.
14. Production code does not use `BigInteger` for the addition calculation.
15. API tests cover successful, invalid, oversized, malformed, and concurrent requests.

## 8. Out-of-Scope Decisions

The following are intentionally not part of this feature:

- Negative integer arithmetic.
- Decimal arithmetic.
- Arbitrary-precision numeric JSON values.
- Authentication and authorization.
- Persistence or audit history.
- API rate limiting and quotas.
- Batch addition of more than two operands.
- Alternative response formats such as XML.
- Returning educational calculation steps from the REST API.
- Backward compatibility for undocumented routes.
- A Spring Boot version upgrade. The repository currently uses Spring Boot `3.1.6`, while the coding rules request `3.3+`; this should be handled as a separate explicitly approved change unless implementation requires it.

## 9. Test Scenarios

### Successful requests

- `123 + 456` returns `579`.
- Different-length operands, such as `1234 + 897`, return the correct result.
- Carry propagation, such as `999 + 1`, returns `1000`.
- Maximum carry sequence, such as `999999 + 1`, returns `1000000`.
- Leading-zero operands, such as `000123 + 000456`, return `579`.
- All-zero operands return `0`.
- Very large valid operands return the correct string result.
- The response contains `result` and does not contain `steps`.

### Validation failures

- Missing `a`.
- Missing `b`.
- Null `a`.
- Null `b`.
- Empty `a` or `b`.
- Whitespace-only `a` or `b`.
- Leading or trailing whitespace.
- Letters, punctuation, or symbols.
- Negative values.
- Decimal values.
- Comma-separated values.
- Plus-prefixed values.
- Numeric JSON values instead of strings.
- Operand length just over the configured limit.
- Malformed JSON.
- Unsupported content type.

### Compatibility and safety

- Existing HTML GET and POST flows remain functional.
- Multiple concurrent requests return their own correct results.
- Logs contain no raw operands, full results, or calculation steps.
- The service rejects oversized inputs without attempting the calculation.

## 10. Open Questions

1. Should unknown JSON properties be rejected or ignored?
2. Should an oversized request return `413 Payload Too Large` or `400 Bad Request`?
3. Is `100,000` digits per operand an appropriate production limit?
4. Should the API expose a configurable limit through application configuration?
5. Should the API include a request ID in every response, or only when errors occur?
6. Should the existing core `MyBigNumber` history API be refactored for thread safety as part of this feature?
7. Should the Spring Boot `3.1.6` to `3.3+` upgrade be included in this work or tracked separately?
8. Should unknown fields be tolerated for client forward compatibility?
9. Is the API intended for internal use only, or will it be exposed outside a trusted network?
10. Are API-level rate limits or authentication required before deployment?
