# Problem4J Spring Web

[![Build Status](https://github.com/malczuuu/problem4j-spring-web/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/malczuuu/problem4j-spring-web/actions/workflows/gradle-build.yml)
[![Sonatype](https://img.shields.io/maven-central/v/io.github.malczuuu.problem4j/problem4j-spring-web)](https://central.sonatype.com/artifact/io.github.malczuuu.problem4j/problem4j-spring-web)
[![License](https://img.shields.io/github/license/malczuuu/problem4j-spring-web)](https://github.com/malczuuu/problem4j-spring-web/blob/main/LICENSE)

> Part of [`problem4j`][problem4j] package of libraries.

Spring Web integration module for [`problem4j-core`][problem4j-core]. library that integrates the RFC Problem Details
model with exception handling in Spring Boot.

The desired usage of this library is to make all your custom exceptions extend `ProblemException` from `problem4j-core`.
It's still possible to create custom `@RestControllerAdvice`-s, but some nuances with `@Order` are necessary (explained
in [Usage](#usage) chapter, which covers also how response bodies for build-in Spring exceptions are overwritten).

## Table of Contents

- [Features](#features)
- [Example](#example)
- [Usage](#usage)
- [Configuration](#configuration)
- [Built-in Spring Exception Mappings](#built-in-spring-exception-mappings)
- [Problem4J Links](#problem4j-links)

## Features

- ✅ Automatic mapping of exceptions to responses with `Problem` objects compliant with [RFC 7807][rfc7807].
- ✅ Mapping of exceptions extending `ProblemException` to responses with `Problem` objects.
- ✅ Fallback mapping of `Exception` to `Problem` objects representing `500 Internal Server Error`.
- ✅ Simple configuration thanks to Spring Boot autoconfiguration.

## Example

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
   <dependencies>
       <dependency>
           <groupId>io.github.malczuuu.problem4j</groupId>
           <artifactId>problem4j-spring-web</artifactId>
           <version>${problem4j-spring-web.version}</version>
       </dependency>
   </dependencies>
   ```
2. Gradle (Groovy or Kotlin DSL):
   ```groovy
   dependencies {
       implementation("io.github.malczuuu.problem4j:problem4j-spring-web:${problem4j-spring-web.version}")
   }
   ```

Overriding of build-in exceptions is performed by custom [`ExceptionMapping`][ExceptionMapping] and its implementations.
These mappings are instantiated in [`ExceptionMappingConfiguration`][ExceptionMappingConfiguration] with
`@ConditionalOnClass`, per appropriate exception. Therefore, if using this library with previous versions, mappings for
exception classes that are not present in classpath are silently ignored.

While creating your own `@RestControllerAdvice`, make sure to position it with right `@Order`. In order for your custom
implementation to work seamlessly, make sure to position it on at least **`Ordered.LOWEST_PRECEDENCE - 1`** (the lower
the value, the higher the priority), as **`ExceptionAdvice`** covers the most generic **`Exception`** class.

| `@RestControllerAdvice`              | covered exceptions             | `@Order(...)`                    |
|--------------------------------------|--------------------------------|----------------------------------|
| `ProblemEnhancedExceptionHandler`    | Spring's internal exceptions   | `Ordered.LOWEST_PRECEDENCE - 10` |
| `ProblemExceptionAdvice`             | `ProblemException`             | `Ordered.LOWEST_PRECEDENCE - 10` |
| `ConstraintViolationExceptionAdvice` | `ConstraintViolationException` | `Ordered.LOWEST_PRECEDENCE - 10` |
| `ExceptionAdvice`                    | `Exception`                    | `Ordered.LOWEST_PRECEDENCE`      |

## Configuration

Library can be configured with following properties.

### `problem4j.detail-format`

Property that specifies how exception handling imported with this module should print `"detail"` field of `Problem`
model (`lowercase`, **`capitalized` - default**, `uppercase`). Useful for keeping the same style of errors coming from
library and your application.

## Built-in Spring Exception Mappings

This module overrides Spring MVC's default (often minimal or plain-text) responses for many framework exceptions and
produces structured RFC 7807 `Problem` objects. [`ExceptionMappingConfiguration`][ExceptionMappingConfiguration]
registers these mappings.

These error mappings to disallow leaking too much information to the client application. It more information is
necessary, feel free to override specific [`ExceptionMapping`][ExceptionMapping], register it as `@Serivce`,
`@Component` or `@Bean` and exclude specific nested[`ExceptionMappingConfiguration`][ExceptionMappingConfiguration]
configuration class.

Overriding whole `ProblemEnhancedExceptionHandler` is not recommended, although such necessities are sometimes
understandable.

### `AsyncRequestTimeoutException`

What triggers it: An async request (e.g. `DeferredResult`, `Callable`, WebAsync) exceeded configured timeout before
producing a value.

Mapping: [`AsyncRequestTimeoutMapping`][AsyncRequestTimeoutMapping]

Example:

```json
{
  "status": 500,
  "title": "Internal Server Error"
}
```

### `ConversionNotSupportedException`

What triggers it: Spring's type conversion system could not convert a controller method return value or property to the
required type (fatal conversion issue, not just a simple mismatch).

Mapping: [`ConversionNotSupportedMapping`][ConversionNotSupportedMapping]

Example:

```json
{
  "status": 500,
  "title": "Internal Server Error"
}
```

### `ErrorResponseException`

What triggers it: Explicitly thrown `ErrorResponseException` (or subclasses like `ResponseStatusException`). Each of
these exceptions carry HTTP status within it as well as details to be used in `application/problem+json` response.

Mapping: [`ErrorResponseMapping`][ErrorResponseMapping]

Example:

```json
{
  "type": "https://example.org/problem-type",
  "title": "Some Error",
  "status": 400,
  "detail": "Explanation of the error",
  "instance": "https://example.org/instances/123",
  "extraKey": "extraValue"
}
```

### `HttpMediaTypeNotAcceptableException`

What triggers it: Client `Accept` header doesn't match any producible media type from controller.

Mapping: [`HttpMediaTypeNotAcceptableMapping`][HttpMediaTypeNotAcceptableMapping]

```json
{
  "status": 406,
  "title": "Not Acceptable"
}
```

### `HttpMediaTypeNotSupportedException`

What triggers it: Request `Content-Type` not supported by any `HttpMessageConverter` for the target endpoint.

Mapping: [`HttpMediaTypeNotSupportedMapping`][HttpMediaTypeNotSupportedMapping]

```json
{
  "status": 415,
  "title": "Unsupported Media Type"
}
```

### `HttpMessageNotReadableException`

What triggers it: Incoming request body couldn't be parsed/deserialized (malformed JSON, wrong structure, EOF, etc.).

Mapping: [`HttpMessageNotReadableMapping`][HttpMessageNotReadableMapping]

```json
{
  "status": 400,
  "title": "Bad Request"
}
```

### `HttpMessageNotWritableException`

What triggers it: Server failed to serialize the response body (e.g. Jackson serialization problem) after controller
returned a value.

Mapping: [`HttpMessageNotWritableMapping`][HttpMessageNotWritableMapping]

```json
{
  "status": 500,
  "title": "Internal Server Error"
}
```

### `HttpRequestMethodNotSupportedException`

What triggers it: HTTP method not supported for a particular endpoint (e.g. `POST` to an endpoint allowing only `GET`).

Mapping: [`HttpRequestMethodNotSupportedMapping`][HttpRequestMethodNotSupportedMapping]

```json
{
  "status": 405,
  "title": "Method Not Allowed"
}
```

### `MaxUploadSizeExceededException`

What triggers it: Multipart upload exceeds configured max file or request size.

Mapping: [`MaxUploadSizeExceededMapping`][MaxUploadSizeExceededMapping]

```json
{
  "status": 413,
  "title": "Content Too Large",
  "detail": "Max upload size exceeded",
  "max": 1048576
}
```

### `MethodArgumentNotValidException`

What triggers it: Bean Validation (JSR 380) failed for a `@Valid` annotated argument (e.g. request body DTO) during data
binding.

Mapping: [`MethodArgumentNotValidMapping`][MethodArgumentNotValidMapping]

```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "Validation failed",
  "errors": [
    {
      "field": "email",
      "error": "must be a well-formed email address"
    },
    {
      "field": "age",
      "error": "must be greater than or equal to 18"
    }
  ]
}
```

Field names convention may be formatted (e.g. `snake_case`) by configuring `spring.jackson.property-naming-strategy`.

### `MissingPathVariableException`

What triggers it: A required URI template variable was not provided (e.g. handler expected `{id}` path variable that was
absent in request mapping resolution).

Mapping: [`MissingPathVariableMapping`][MissingPathVariableMapping]

```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "Missing path variable",
  "name": "id"
}
```

### `MissingServletRequestParameterException`

What triggers it: Required query parameter is missing (e.g. `@RequestParam(required=true)` not supplied by client).

Mapping: [`MissingServletRequestParameterMapping`][MissingServletRequestParameterMapping]

```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "Missing request param",
  "param": "q",
  "kind": "string"
}
```

### `MissingServletRequestPartException`

What triggers it: Required multipart request part missing (e.g. file field in a multipart/form-data POST not provided).

Mapping: [`MissingServletRequestPartMapping`][MissingServletRequestPartMapping]

```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "Missing request part",
  "param": "file"
}
```

### `NoHandlerFoundException`

What triggers it: DispatcherServlet could not find any handler (no matching controller) for the request (requires
`throwExceptionIfNoHandlerFound=true`).

Mapping: [`NoHandlerFoundMapping`][NoHandlerFoundMapping]

```json
{
  "status": 404,
  "title": "Not Found"
}
```

### `NoResourceFoundException`

What triggers it: Static resource handling (e.g. `ResourceHttpRequestHandler`) couldn't resolve the requested resource (
Spring Boot 3.x when resource chain handling is enabled).

Mapping: [`NoResourceFoundMapping`][NoResourceFoundMapping]

```json
{
  "status": 404,
  "title": "Not Found"
}
```

### `ServletRequestBindingException`

What triggers it: General binding issues with request parameters, headers, path variables (e.g. missing header required
by `@RequestHeader`).

Mapping: [`ServletRequestBindingMapping`][ServletRequestBindingMapping]

```json
{
  "status": 400,
  "title": "Bad Request"
}
```

### `TypeMismatchException`

What triggers it: Failed to bind a web request parameter/path variable to a controller argument due to type mismatch (
e.g. `age=abc` where `age` expects an integer).

Mapping: [`TypeMismatchMapping`][TypeMismatchMapping]

```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "Type mismatch",
  "property": "age",
  "kind": "integer"
}
```

### Fallback / Unknown Exceptions

What triggers it: Any unhandled exception flowing through `ResponseEntityExceptionHandler` without a dedicated mapping.
Result example:

```json
{
  "status": 500,
  "title": "Internal Server Error"
}
```

## Problem4J Links

- [`problem4j`][problem4j] - Documentation repository.
- [`problem4j-core`][problem4j-core] - Core library defining `Problem` model and `ProblemException`.
- [`problem4j-jackson`][problem4j-jackson] - Jackson module for serializing and deserializing `Problem` objects.
- [`problem4j-spring-web`][problem4j-spring-web] - Spring Web module extending `ResponseEntityExceptionHandler` for
  handling exceptions and returning `Problem` responses.

[ExceptionMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/ExceptionMapping.java

[ExceptionMappingConfiguration]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/ExceptionMappingConfiguration.java

[AsyncRequestTimeoutMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/AsyncRequestTimeoutMapping.java

[ConversionNotSupportedMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/ConversionNotSupportedMapping.java

[ErrorResponseMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/ErrorResponseMapping.java

[HttpMediaTypeNotAcceptableMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/HttpMediaTypeNotAcceptableMapping.java

[HttpMediaTypeNotSupportedMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/HttpMediaTypeNotSupportedMapping.java

[HttpMessageNotReadableMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/HttpMessageNotReadableMapping.java

[HttpMessageNotWritableMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/HttpMessageNotWritableMapping.java

[HttpRequestMethodNotSupportedMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/HttpRequestMethodNotSupportedMapping.java

[MaxUploadSizeExceededMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/MaxUploadSizeExceededMapping.java

[MethodArgumentNotValidMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/MethodArgumentNotValidMapping.java

[MissingPathVariableMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/MissingPathVariableMapping.java

[MissingServletRequestParameterMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/MissingServletRequestParameterMapping.java

[MissingServletRequestPartMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/MissingServletRequestPartMapping.java

[NoHandlerFoundMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/NoHandlerFoundMapping.java

[NoResourceFoundMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/NoResourceFoundMapping.java

[ServletRequestBindingMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/ServletRequestBindingMapping.java

[TypeMismatchMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/TypeMismatchMapping.java

[maven-central]: https://central.sonatype.com/artifact/io.github.malczuuu.problem4j/problem4j-spring-web

[problem4j]: https://github.com/malczuuu/problem4j

[problem4j-core]: https://github.com/malczuuu/problem4j-core

[problem4j-spring-web]: https://github.com/malczuuu/problem4j-spring-web

[problem4j-jackson]: https://github.com/malczuuu/problem4j-jackson

[rfc7807]: https://datatracker.ietf.org/doc/html/rfc7807
