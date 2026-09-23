# Add2Num
Project Add 2 numbers

This repository contains two modules:

- `core`: the domain library implementing `MyBigNumber` (grade-school addition for arbitrarily large integers) and unit tests.
- `web`: a Spring Boot web app that provides the Thymeleaf UI and a JSON REST API using the `core` library.

REST API
--------

The project provides a versioned API for adding two large, non-negative integer strings.

```http
POST /api/v1/add
Content-Type: application/json
Accept: application/json
```

Request:

```json
{
  "a": "123",
  "b": "456"
}
```

Success response (`200 OK`):

```json
{
  "result": "579"
}
```

The API accepts only ASCII digits. It accepts leading zeros and returns a normalized result. It rejects blank, negative, decimal, formatted, and non-digit values. It does not return calculation steps.

The API has these protections:

- Each operand can contain up to 100,000 digits.
- The JSON request body can be up to 200,032 bytes.
- Oversized requests return `413 Payload Too Large`.
- The API does not use `BigInteger` for production calculation.
- REST calculations do not create calculation-history logs.
- Error responses do not expose raw input values or stack traces.

The existing Thymeleaf UI remains available at `http://localhost:8080` and keeps its step-by-step calculation history.

Quick start (build, run, tests)

Requirements:

- Java 17+ (tested with Java 21)
  [Link adoptium v21](https://adoptium.net/temurin/releases/?version=21)
- Maven 3.9+
  [Download maven-3.9.16 (zip)](https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip)

From the project root run:

```powershell
mvn clean package
```

Run unit tests for the core only (Task 1 - test case):

```powershell
mvn -pl core test
```

Run all module tests:

```powershell
mvn test
```

The tests cover the REST API, validation errors, large numbers, request-size limits, concurrent requests, and regression checks for the existing HTML UI.
Run the web application (verified sequence) (Task 2 - Web UI):

```powershell
mvn clean package
java -jar web\target\web-0.0.1.jar
```

Then open `http://localhost:8080` in your browser.

Developer notes

- From the project root, rebuild and run the Spring Boot app with both modules:

```powershell
mvn clean install
mvn -pl web -am spring-boot:run
```

Rebuild both modules after changing the `core` API. This prevents stale `core` and `web` classes from causing `NoSuchMethodError` responses.

Project structure

```
Add2Num/
├─ pom.xml                      # parent aggregator (packaging=pom)
├─ core/
│  ├─ pom.xml
│  └─ src/main/java/mci/vietnam/splam/core/domain/
│     ├─ MyBigNumber.java
│     └─ BigNumberResult.java
├─ web/
│  ├─ pom.xml
│  └─ src/main/java/mci/vietnam/splam/web/
│     ├─ Add2NumApplication.java
│     ├─ config/
│     ├─ controller/
│     │  ├─ AdditionController.java       # Thymeleaf UI
│     │  └─ AdditionRestController.java  # JSON REST API
│     └─ service/
└─ README.md
```