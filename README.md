# Problem4J Spring

[![Build Status](https://github.com/malczuuu/problem4j-spring/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/malczuuu/problem4j-spring/actions/workflows/gradle-build.yml)
[![Sonatype](https://img.shields.io/maven-central/v/io.github.malczuuu.problem4j/problem4j-spring-bom)](https://central.sonatype.com/artifact/io.github.malczuuu.problem4j/problem4j-spring-bom)
[![License](https://img.shields.io/github/license/malczuuu/problem4j-spring)](https://github.com/malczuuu/problem4j-spring/blob/main/LICENSE)

Spring integration module for [`problem4j-core`][problem4j-core]. library that integrates the RFC Problem Details model
with exception handling in Spring Boot.

## Table of Contents

- [Features](#features)
- [Example](#example)
- [Usage](#usage)
- [Configuration](#configuration)
- [Problem4J Links](#problem4j-links)

## Features

- ✅ Automatic mapping of exceptions to responses with `Problem` objects compliant with [RFC 7807][rfc7807].
- ✅ Mapping of exceptions extending `ProblemException` to responses with `Problem` objects.
- ✅ Fallback mapping of `Exception` to `Problem` objects representing `500 Internal Server Error`.
- ✅ Simple configuration thanks to Spring Boot autoconfiguration.

## Example

The general idea is to make all exceptions in your application to originate from `ProblemException`. This way, you can
assign an appropriate `Problem` response to each exception.

If that's not possible, add a custom `@RestControllerAdvice` that returns a `Problem` object, but take note at `@Order`
as explained in [Usage](#usage) chapter.

```java
import io.github.malczuuu.problem4j.core.Problem;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Order(Ordered.LOWEST_PRECEDENCE - 20)
@Component
@RestControllerAdvice
public class ExampleExceptionAdvice {

  @ExceptionHandler(ExampleException.class)
  public ResponseEntity<Problem> method(ExampleException ex, WebRequest request) {
    Problem problem =
        Problem.builder()
            .type("http://example.com/errors/example-error")
            .title("Example Title")
            .status(400)
            .detail(ex.getMessage())
            .instance("https://example.com/instances/example-instance")
            .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    HttpStatus status = HttpStatus.valueOf(problem.getStatus());

    return new ResponseEntity<>(problem, headers, status);
  }
}
```

## Usage

Add library as dependency to Maven or Gradle. See the actual versions on [Maven Central][maven-central]. Add it along
with repository in your dependency manager. **Java 17** or higher is required to use this library.

Tested with Spring Boot `3+`, but mostly on `3.5.x`. However, the idea for `v1.x` of this library was to be backwards
compatible down to `3.0.0`. Integration with Spring Boot 4 (once its released) will most likely be released as `v2.x` if
`v1.x` won't be compatible.

1. Maven:
   ```xml
   <dependencyManagement>
       <dependencies>
           <dependency>
               <groupId>io.github.malczuuu.problem4j</groupId>
               <artifactId>problem4j-spring-bom</artifactId>
               <version>${problem4j-spring-bom.version}</version>
               <type>pom</type>      
               <scope>import</scope> 
           </dependency>
       </dependencies>
   </dependencyManagement>
   <dependencies>
       <!-- pick the one for your project -->
       <dependency>
           <groupId>io.github.malczuuu.problem4j</groupId>
           <artifactId>problem4j-spring-webflux</artifactId>
       </dependency>
       <dependency>
           <groupId>io.github.malczuuu.problem4j</groupId>
           <artifactId>problem4j-spring-webmvc</artifactId>
       </dependency>
   </dependencies>
   ```
2. Gradle (Groovy or Kotlin DSL):
   ```groovy
   dependencies {
       implementation(platform("io.github.malczuuu.problem4j:problem4j-spring-bom:${problem4j-spring-bom.version}"))
   
       // pick the one for your project
       implementation("io.github.malczuuu.problem4j:problem4j-spring-webflux")
       implementation("io.github.malczuuu.problem4j:problem4j-spring-webmvc")
   }
   ```

Details on library usability can be found in [`problem4j-spring-web/README.md`][problem4j-spring-web-readme].

While creating your own `@RestControllerAdvice`, make sure to position it with right `@Order`. In order for your custom
implementation to work seamlessly, make sure to position it on at least **`Ordered.LOWEST_PRECEDENCE - 1`** (the lower
the value, the higher the priority), as **`ExceptionAdvice`** covers the most generic **`Exception`** class.

| <center>covered exceptions</center> | <center>`@Order(...)`</center>   |
|-------------------------------------|----------------------------------|
| Spring's internal exceptions        | `Ordered.LOWEST_PRECEDENCE - 10` |
| `ConstraintViolationException`      | `Ordered.LOWEST_PRECEDENCE - 10` |
| `DecodingException`                 | `Ordered.LOWEST_PRECEDENCE - 10` |
| `ProblemException`                  | `Ordered.LOWEST_PRECEDENCE - 10` |
| `Exception`                         | `Ordered.LOWEST_PRECEDENCE`      |

## Configuration

Library can be configured with following properties.

### `problem4j.detail-format`

Property that specifies how exception handling imported with this module should print `"detail"` field of `Problem`
model (`lowercase`, **`capitalized` - default**, `uppercase`). Useful for keeping the same style of errors coming from
library and your application.

## Problem4J Links

- [`problem4j-core`][problem4j-core] - Core library defining `Problem` model and `ProblemException`.
- [`problem4j-jackson`][problem4j-jackson] - Jackson module for serializing and deserializing `Problem` objects.
- [`problem4j-spring`][problem4j-spring] - Spring modules extending `ResponseEntityExceptionHandler` for handling
  exceptions and returning `Problem` responses.

[maven-central]: https://central.sonatype.com/artifact/io.github.malczuuu.problem4j/problem4j-spring-bom

[problem4j-core]: https://github.com/malczuuu/problem4j-core

[problem4j-spring]: https://github.com/malczuuu/problem4j-spring

[problem4j-spring-web-readme]: problem4j-spring-web/README.md

[problem4j-jackson]: https://github.com/malczuuu/problem4j-jackson

[rfc7807]: https://datatracker.ietf.org/doc/html/rfc7807
