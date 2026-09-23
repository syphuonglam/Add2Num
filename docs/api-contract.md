# Add2Num REST API Contract

## Endpoint

| Item | Contract |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/add` |
| Request content type | `application/json` |
| Response content type | `application/json` |
| Success status | `200 OK` |
| Calculation steps | Not included |

The endpoint is versioned. The existing Thymeleaf routes remain unchanged:

- `GET /`
- `GET /index`
- `POST /add`

## Request

### Headers

```http
Content-Type: application/json
Accept: application/json
```

### Body

```json
{
  "a": "123",
  "b": "456"
}
```

Both fields are required JSON strings containing only ASCII decimal digits before normalization.

The request must not use JSON numeric values:

```json
{
  "a": 123,
  "b": 456
}
```

That request is invalid because the API contract requires strings.

Unknown JSON properties must be handled consistently. **OPEN QUESTION:** reject unknown properties with `400 Bad Request`, or ignore them for forward compatibility.

## Successful Response

Status:

```http
200 OK
Content-Type: application/json
```

Body:

```json
{
  "result": "579"
}
```

The result is canonicalized:

- Only ASCII digits are returned.
- Leading zeros are removed.
- An all-zero result is returned as `"0"`.
- The response does not contain calculation steps.

Example with leading zeros:

```json
{
  "a": "000123",
  "b": "000456"
}
```

```json
{
  "result": "579"
}
```

## Validation Errors

Validation failures return `400 Bad Request` with the stable error shape below.

```http
400 Bad Request
Content-Type: application/json
```

Example:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed.",
  "field": "a",
  "requestId": "8f3c3f19-1f39-4b8a-8f37-9cda0e4b16c2"
}
```

### Field-level validation messages

Messages must not include submitted operand values.

| Condition | `field` | `code` | Recommended `message` |
| --- | --- | --- | --- |
| `a` or `b` is missing | Missing field name | `VALIDATION_ERROR` | `Field is required.` |
| `a` or `b` is JSON `null` | Field name | `VALIDATION_ERROR` | `Field must not be null.` |
| Empty string | Field name | `VALIDATION_ERROR` | `Field must not be blank.` |
| Whitespace-only string | Field name | `VALIDATION_ERROR` | `Field must not be blank.` |
| Leading or trailing whitespace | Field name | `VALIDATION_ERROR` | `Field must contain digits only.` |
| Non-ASCII digit or other non-digit | Field name | `VALIDATION_ERROR` | `Field must contain ASCII digits only.` |
| Negative value | Field name | `VALIDATION_ERROR` | `Field must be a non-negative integer.` |
| Decimal, formatted, or plus-prefixed value | Field name | `VALIDATION_ERROR` | `Field must contain ASCII digits only.` |
| Numeric JSON value instead of string | Field name | `VALIDATION_ERROR` | `Field must be a JSON string.` |

The server may use one message for multiple invalid character cases, but the `code` must remain stable and the response must not expose the invalid value.

## Malformed JSON

Malformed JSON returns `400 Bad Request`:

```json
{
  "code": "MALFORMED_JSON",
  "message": "Request body contains malformed JSON.",
  "requestId": "8f3c3f19-1f39-4b8a-8f37-9cda0e4b16c2"
}
```

The response must not include parser details, stack traces, class names, or the raw request body.

## Oversized Input

The maximum operand length is `100,000` decimal digits, measured before leading-zero normalization. The maximum JSON request body is `200,032` bytes. Oversized input must be rejected before calculation and must use:

```json
{
  "code": "INPUT_TOO_LARGE",
  "message": "Input exceeds the maximum allowed length.",
  "field": "a",
  "requestId": "8f3c3f19-1f39-4b8a-8f37-9cda0e4b16c2"
}
```

**OPEN QUESTION:** The final implementation must consistently choose either `413 Payload Too Large` or `400 Bad Request` for this case. The specification currently permits either status.

Requests exceeding the HTTP body limit return `413 Payload Too Large` with code `INPUT_TOO_LARGE` and message `Request body exceeds the maximum allowed size.`. The response does not identify a field because parsing has not started.

## Other HTTP Errors

| Situation | Status | Code |
| --- | ---: | --- |
| Unsupported `Content-Type` | `415 Unsupported Media Type` | `UNSUPPORTED_MEDIA_TYPE` |
| No acceptable JSON response can be produced | `406 Not Acceptable` | `NOT_ACCEPTABLE` |
| Unexpected server failure | `500 Internal Server Error` | `INTERNAL_ERROR` |

Example internal error:

```json
{
  "code": "INTERNAL_ERROR",
  "message": "An unexpected error occurred.",
  "requestId": "8f3c3f19-1f39-4b8a-8f37-9cda0e4b16c2"
}
```

Internal errors must not expose implementation details.

## Error Response Schema

```json
{
  "code": "string",
  "message": "string",
  "field": "string, optional",
  "requestId": "string, optional"
}
```

Rules:

- `code` is stable and machine-readable.
- `message` is safe for clients and contains no raw operands or full results.
- `field` is present when an individual request field is responsible.
- `requestId` is optional in the response and must never contain sensitive data.
- Error responses must not include stack traces, class names, internal exception messages, or calculation steps.

## Content-Type and Accept Requirements

- Requests must use `Content-Type: application/json`.
- Unsupported request media types return `415`.
- Clients should send `Accept: application/json`.
- If the client requests a representation that cannot be produced, return `406`.

## Correlation and Request IDs

A request ID may be returned in `requestId` for correlation. When present:

- It must be safe, opaque, and non-sensitive.
- It should be included in server-side error logs.
- It must not include operand values, result values, or calculation steps.
- It may be supplied by an existing trusted infrastructure layer or generated by the application.

**OPEN QUESTION:** Decide whether every response must include a request ID or whether request IDs are required only for errors. The current contract treats the field as optional.

## Thread-Safety and Privacy Constraints

- The endpoint must not store request data in mutable singleton state.
- Concurrent requests must not share result or calculation-history state.
- The REST response does not expose calculation history.
- Production logs must not contain raw operands, full results, or per-digit calculation steps.
