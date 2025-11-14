# Problem4J Spring

[![Build Status](https://github.com/malczuuu/problem4j-spring/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/malczuuu/problem4j-spring/actions/workflows/gradle-build.yml)
[![Sonatype](https://img.shields.io/maven-central/v/io.github.malczuuu.problem4j/problem4j-spring-bom?filter=1.1.*)][maven-central]
[![License](https://img.shields.io/github/license/malczuuu/problem4j-spring)](https://github.com/malczuuu/problem4j-spring/blob/main/LICENSE)

Designing clear and consistent error responses in a REST API is often harder than it looks. Without a shared standard,
each application ends up inventing its own ad-hoc format, which quickly leads to inconsistency and confusion.
[RFC 7807 - Problem Details for HTTP APIs][rfc7807] solves this by defining a simple, extensible JSON structure for
error messages.

**Problem4J** brings this specification into the Spring ecosystem, offering a practical way to model, throw, and handle
API errors using `Problem` objects. It helps you enforce a consistent error contract across your services, while staying
flexible enough for custom exceptions and business-specific details.

## Table of Contents

- [Why bother with Problem4J](#why-bother-with-problem4j)
- [Usage](#usage)
- [Repository](#repository)
- [Problem4J Links](#problem4j-links)

## Why bother with Problem4J

Even though Spring provides `ProblemDetail` and `ErrorResponseException` for [**RFC 7807**][rfc7807]-compliant error
responses, they are quite rough, minimalistic, and often require manual population of fields. In contrast, **Problem4J**
was created to:

- Provide a **fully immutable, fluent `Problem` model** with support for extensions.
- Support **declarative exception mapping** via `@ProblemMapping` or **programmatic one** via `ProblemException` and
  `ProblemResolver`.
- Automatically **interpolate exception fields and context metadata** (e.g., `traceId`) into responses.
- Offer **consistent error responses** across WebMVC and WebFlux, including validation and framework exceptions.
- Allow **custom extensions** without boilerplate, making structured errors easier to trace and consume.
- Configure painlessly thanks to Spring Boot autoconfiguration.

Problem4J is designed for robust, traceable, and fully configurable REST API errors.

## Usage

Extensive usage manual explaining library features can be found on [repository wiki pages][repository-wiki-pages].

Add library as dependency to Maven or Gradle. See the actual versions on [Maven Central][maven-central]. Add it along
with repository in your dependency manager. **Java 17** or higher is required to use this library.

It's assumed that Spring Boot will be already present in your project dependencies, as `problem4j-spring` is only an
extension to it and does not bring it transitively.

1. Maven:
   ```xml
   <dependencies>
       <!-- pick the one for your project -->
       <dependency>
           <groupId>io.github.malczuuu.problem4j</groupId>
           <artifactId>problem4j-spring-webflux</artifactId>
           <version>${version}</version>
       </dependency>
       <dependency>
           <groupId>io.github.malczuuu.problem4j</groupId>
           <artifactId>problem4j-spring-webmvc</artifactId>
           <version>${version}</version>
       </dependency>
   </dependencies>
   ```
2. Gradle (Kotlin DSL):
   ```groovy
   dependencies {
       // pick the one for your project
       implementation("io.github.malczuuu.problem4j:problem4j-spring-webflux:${version}")
       implementation("io.github.malczuuu.problem4j:problem4j-spring-webmvc:${version}")
   }
   ```

## Repository

This repository maintains two major versions, supporting Spring Boot 3 and 4. The goal is to maintain both versions at
least until Spring Boot 3 reaches its end of life or becomes irrelevant.

| branch           | info                                       | latest                                                                                                                              |
|------------------|--------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| `main`           | version `2.x` supporting Spring Boot `4.x` | [![Sonatype](https://img.shields.io/maven-central/v/io.github.malczuuu.problem4j/problem4j-spring-bom)][maven-central]              |
| `release-v1.*.x` | version `1.x` supporting Spring Boot `3.x` | [![Sonatype](https://img.shields.io/maven-central/v/io.github.malczuuu.problem4j/problem4j-spring-bom?filter=1.0.*)][maven-central] |

## Problem4J Links

- [`problem4j-core`][problem4j-core] - Core library defining `Problem` model and `ProblemException`.
- [`problem4j-jackson`][problem4j-jackson] - Jackson module for serializing and deserializing `Problem` objects.
- [`problem4j-spring`][problem4j-spring] - Spring modules extending `ResponseEntityExceptionHandler` for handling
  exceptions and returning `Problem` responses.

[maven-central]: https://central.sonatype.com/namespace/io.github.malczuuu.problem4j

[problem4j-core]: https://github.com/malczuuu/problem4j-core

[problem4j-jackson]: https://github.com/malczuuu/problem4j-jackson

[problem4j-spring]: https://github.com/malczuuu/problem4j-spring

[repository-wiki-pages]: https://github.com/malczuuu/problem4j-spring/wiki

[rfc7807]: https://datatracker.ietf.org/doc/html/rfc7807
