# Problem4J Spring

[![Build Status](https://github.com/malczuuu/problem4j-spring/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/malczuuu/problem4j-spring/actions/workflows/gradle-build.yml)
[![Sonatype](https://img.shields.io/maven-central/v/io.github.malczuuu.problem4j/problem4j-spring-bom)][maven-central]
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
- [Features](#features)
- [Usage](#usage)
- [Configuration](#configuration)
- [Problem4J Links](#problem4j-links)

## Why bother with Problem4J

Even though Spring provides `ProblemDetail` and `ErrorResponseException` for **RFC 7807**-compliant error responses,
they are quite rough, minimal, and often require manual population of fields. In contrast, **Problem4J** was created to:

- Provide a **fully immutable, fluent `Problem` model** with support for extensions.
- Support **declarative exception mapping** via `@ProblemMapping` or **programmatic one** via `ProblemException` and
  `ProblemResolver`.
- Automatically **interpolate exception fields and context metadata** (e.g., `traceId`) into responses.
- Offer **consistent error responses** across WebMVC and WebFlux, including validation and framework exceptions.
- Allow **custom extensions** without boilerplate, making structured errors easier to trace and consume.

In short, Problem4J is designed for developers who want **robust, traceable, and fully configurable REST API errors**,
while keeping everything RFC 7807-compliant.

## Features

This module provides Spring integration for [`problem4j-core`][problem4j-core]. library that integrates the RFC Problem
Details model with exception handling in Spring Boot.

- ✅ Automatic mapping of exceptions to responses with `Problem` objects compliant with [RFC 7807][rfc7807].
- ✅ Mapping of exceptions extending `ProblemException` to responses with `Problem` objects.
- ✅ Mapping of exceptions annotated with `@ProblemMapping` to responses with `Problem` objects.
- ✅ Fallback mapping of `Exception` to `Problem` objects representing `500 Internal Server Error`.
- ✅ Simple configuration thanks to Spring Boot autoconfiguration.

## Usage

The library provides two ways to convert exceptions into RFC 7807-compliant `Problem` responses. You can either extend
`ProblemException` or use `@ProblemMapping` annotation on your own exception if modifying inheritance tree is not an
option for.

For more details and usage examples, see the submodule `README.md` files:

- [`problem4j-spring-web/README.md`][problem4j-spring-web-readme] - base features of Spring integration of Problem4J,
- [`problem4j-spring-webflux/README.md`][problem4j-spring-webflux-readme] - Spring WebFlux tweaks and specifics,
- [`problem4j-spring-webmvc/README.md`][problem4j-spring-webmvc-readme] - Spring WebMVC tweaks and specifics.

Add library as dependency to Maven or Gradle. See the actual versions on [Maven Central][maven-central]. Add it along
with repository in your dependency manager. **Java 17** or higher is required to use this library.

Tested mostly with Spring Boot from `3.3.x` to `3.5.x`. However, the idea for `problem4j-spring-v1.x` is to be backwards
compatible down to Spring Boot `3.0.0`. Integration with **Spring Boot 4** (once its released) will most likely be
released as `problem4j-spring-v2.x`, and maintained on separate branches if `v1.x` won't be compatible.

**Note:** To limit the number of transitive dependencies, you need to include Spring Boot explicitly in your project.

1. Maven:
   ```xml
   <dependencies>
       <!-- pick the one for your project -->
       <dependency>
           <groupId>io.github.malczuuu.problem4j</groupId>
           <artifactId>problem4j-spring-webflux</artifactId>
           <version>1.0.0-alpha3</version>
       </dependency>
       <dependency>
           <groupId>io.github.malczuuu.problem4j</groupId>
           <artifactId>problem4j-spring-webmvc</artifactId>
           <version>1.0.0-alpha3</version>
       </dependency>
   </dependencies>
   ```
2. Gradle (Kotlin DSL):
   ```groovy
   dependencies {
       // pick the one for your project
       implementation("io.github.malczuuu.problem4j:problem4j-spring-webflux:1.0.0-alpha3")
       implementation("io.github.malczuuu.problem4j:problem4j-spring-webmvc:1.0.0-alpha3")
   }
   ```

For using snapshot versions [**Snapshots** chapter of`PUBLISHING.md`](PUBLISHING.md#snapshots).

## Configuration

Library can be configured with following properties.

### `problem4j.detail-format`

Property that specifies how exception handling imported with this module should print `"detail"` field of `Problem`
model (`lowercase`, **`capitalized` - default**, `uppercase`). Useful for keeping the same style of errors coming from
library and your application.

### `problem4j.tracing-header-name`

Property that specifies the name of the HTTP header used for tracing requests. If set, the trace identifier from this
header can be injected into the `Problem` response, for example into the`instance` field when combined with
[`problem4j.instance-override`](#problem4jinstance-override). Defaults to `null` (disabled).

### `problem4j.instance-override`

Property that defines a template for overriding the `instance` field in `Problem` responses.The value may contain the
special placeholder `{traceId}`, which will be replaced at runtime with the trace identifier from the current request (
see [`problem4j.tracing-header-name`](#problem4jtracing-header-name)). Defaults to `null` (no override applied).

For example, by assigning `problem4j.instance-override=/error-instances/{traceId}`, with tracing enabled, each `Problem`
response will have `"instance"` field matching to that format (e.g. `"/error-instances/WQ1tbs12rtSD"`).

## Problem4J Links

- [`problem4j-core`][problem4j-core] - Core library defining `Problem` model and `ProblemException`.
- [`problem4j-jackson`][problem4j-jackson] - Jackson module for serializing and deserializing `Problem` objects.
- [`problem4j-spring`][problem4j-spring] - Spring modules extending `ResponseEntityExceptionHandler` for handling
  exceptions and returning `Problem` responses.

[maven-central]: https://central.sonatype.com/namespace/io.github.malczuuu.problem4j

[problem4j-core]: https://github.com/malczuuu/problem4j-core

[problem4j-spring]: https://github.com/malczuuu/problem4j-spring

[problem4j-spring-web-readme]: problem4j-spring-web/README.md

[problem4j-spring-webflux-readme]: problem4j-spring-webflux/README.md

[problem4j-spring-webmvc-readme]: problem4j-spring-webmvc/README.md

[problem4j-jackson]: https://github.com/malczuuu/problem4j-jackson

[rfc7807]: https://datatracker.ietf.org/doc/html/rfc7807
