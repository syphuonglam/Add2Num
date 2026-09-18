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
Run the web application (verified sequence) (Task 2 - Web UI):

```powershell
mvn clean package
java -jar web\target\web-0.0.1.jar
```

Then open `http://localhost:8080` in your browser.

Developer notes

- You can run the Spring Boot app directly from the `web` module folder where the plugin is declared:

```powershell
cd web
mvn spring-boot:run
```

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
│     └─ controller/AdditionController.java
└─ README.md
```