# Add2Num
Project Add 2 numbers

This repository contains two modules:

- `core`: the domain library implementing `MyBigNumber` (grade-school addition for arbitrarily large integers) and unit tests.
- `web`: a Spring Boot + Thymeleaf demo web app that uses the `core` library and displays step-by-step calculation history.

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