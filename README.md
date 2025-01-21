# Annotation Scanner

## Overview

The Annotation Scanner is a Java-based utility designed to scan JAR files for classes and their associated annotations. It generates a CSV report summarizing the findings, which can be useful for documentation or analysis purposes.

## Features

- Scans JAR files to identify classes and their annotations.
- Generates a CSV report with class names and their corresponding annotations.
- Handles exceptions gracefully, providing informative error messages.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Usage

To run the Annotation Scanner, use the following Maven command:

```bash
mvn clean compile
mvn exec:java
```

This will execute the `AnnotationScanner` class, which is configured in the `pom.xml` file to scan the specified JAR file and generate the report.

## Configuration

- The path to the JAR file to be scanned and the output CSV file can be modified in the `main` method of the `AnnotationScanner` class.

## Testing

The project includes unit tests to ensure functionality. To run the tests, execute:

```bash
mvn test
```

## Code Structure

- **Main Class:** `AnnotationScanner.java`

  - Responsible for scanning the JAR file and generating the report.
  - Key methods include:
    - `generateAnnotationReport`: Initiates the scanning and report generation process.
    - `openJarFile`: Opens the specified JAR file.
    - `createClassLoader`: Creates a class loader for the JAR.
    - `scanJarForAnnotations`: Scans the JAR for annotations.
    - `writeReportToFile`: Writes the generated report to a CSV file.

- **Test Class:** `AnnotationScannerTest.java`
  - Contains unit tests for the main functionalities of the `AnnotationScanner`.
