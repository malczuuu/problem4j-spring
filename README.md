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
- [Usage](#usage)
- [Features](#features)
  - [Returning response bodies from custom exceptions](#returning-response-bodies-from-custom-exceptions)
      - [Extending `ProblemException`](#extending-problemexception)
      - [Annotating `@ProblemMapping`](#annotating-problemmapping)
      - [Implementing `ProblemResolver`](#implementing-problemresolver)
      - [Custom `@RestControllerAdvice` implementation](#custom-restcontrolleradvice)
      - [Spring's build-in `@ResponseStatus` annotation](#springs-build-in-responsestatus-annotation)
      - [Using `problem4j-core`](#using-problem4j-core)
  - [Inspectors for built-in advices](#inspectors-for-built-in-advices)
  - [Validation](#validation)
  - [Occurrences of `TypeMismatchException`](#occurrences-of-typemismatchexception)
  - [Occurrences of `ErrorResponseException`](#occurrences-of-errorresponseexception)
  - [General HTTP Stuff](#general-http-stuff)
- [Experimental Features](#experimental-features)
  - [Overriding Problem Fields](#overriding-problem-fields)
- [Configuration](#configuration)
- [FAQ](#faq)
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

In short, Problem4J is designed for developers who want **robust, traceable, and fully configurable REST API errors**,
while keeping everything [RFC 7807][rfc7807]-compliant.

## Usage

The library provides two ways to convert exceptions into RFC 7807-compliant `Problem` responses. You can either extend
`ProblemException`, mark your exception with `@ProblemMapping` or implement `ProblemResolver` to build `Problem` object
by yourself.

For more details and usage examples, see the [Features](#features) chapter.

Add library as dependency to Maven or Gradle. See the actual versions on [Maven Central][maven-central]. Add it along
with repository in your dependency manager. **Java 17** or higher is required to use this library.

The idea for `problem4j-spring-v1.x` is to be backwards compatible down to Spring Boot `3.0.x`, although it was tested
mostly on versions between `3.2.x` and `3.5.x`. Integration with **Spring Boot 4** (once its released) will most likely
be released as `problem4j-spring-v2.x`, and maintained on separate branches if `v1.x` won't be compatible.

**Note:** To limit the number of transitive dependencies, you need to include Spring Boot explicitly in your project.

1. Maven:
   ```xml
   <dependencies>
       <!-- pick the one for your project -->
       <dependency>
           <groupId>io.github.malczuuu.problem4j</groupId>
           <artifactId>problem4j-spring-webflux</artifactId>
           <version>1.0.0-rc3</version>
       </dependency>
       <dependency>
           <groupId>io.github.malczuuu.problem4j</groupId>
           <artifactId>problem4j-spring-webmvc</artifactId>
           <version>1.0.0-rc3</version>
       </dependency>
   </dependencies>
   ```
2. Gradle (Kotlin DSL):
   ```groovy
   dependencies {
       // pick the one for your project
       implementation("io.github.malczuuu.problem4j:problem4j-spring-webflux:1.0.0-rc3")
       implementation("io.github.malczuuu.problem4j:problem4j-spring-webmvc:1.0.0-rc3")
   }
   ```

Spring Boot's autoconfiguration will automatically load configurations defined in picked module.

For using snapshot versions [**Snapshots** chapter of`PUBLISHING.md`](PUBLISHING.md#snapshots).

## Features

This module replaces Spring Web’s default (often verbose or plain-text) error responses with [RFC 7807][rfc7807]-alike
`Problem` objects for a wide range of framework exceptions. It also provides mechanisms to map your own application
exceptions to `Problem` responses, as described in the following chapters.

To maintain compatibility with multiple Spring Boot versions, the library uses `@ConditionalOnClass` guards around all
components that translate exceptions into HTTP responses. This ensures that if your application runs on an older Spring
Boot version lacking certain exception classes, the configuration is safely skipped instead of causing a
`ClassNotFoundException`.

### Returning response bodies from custom exceptions

There are three ways of returning `application/problem+json` responses from application exceptions. You can either
extend `ProblemException`, annotate your exception with `@ProblemMapping` or implement `ProblemResolver` and declare it
as a component. A few build-in Spring features are also integrated with returning `Problem` objects.

Following subchapters dive deeper into these solutions.

#### Extending `ProblemException`

If you use `ProblemException`, or your exceptions extend `ProblemException`, the library will automatically use the
`Problem` instance provided by the exception when building the response. This is useful when you want full programmatic
control over the `Problem` object.

```java
/**
 * <pre>{@code
 * {
 *   "type": "https://example.org/errors/invalid-request",
 *   "title": "Invalid Request",
 *   "status": 400,
 *   "detail": "not a valid json",
 *   "instance": "https://example.org/instances/1234"
 * }
 * }</pre>
 */
public class Example {
  public void method() {
    Problem problem =
        Problem.builder()
            .type("https://example.org/errors/invalid-request")
            .title("Invalid Request")
            .status(400)
            .detail("not a valid json")
            .instance("https://example.org/instances/1234")
            .build();
    throw new ProblemException(problem);
  }
}
```

For convenience, consider subclassing `ProblemException` and encapsulating building `Problem` object within.

#### Annotating `@ProblemMapping`

For exceptions that cannot extend `ProblemException`, you can annotate them with `@ProblemMapping`. This allows you to
declaratively map exception fields to a `Problem`.

To extract values from target exception, it's possible to use placeholders for interpolation.

- `{message}` - the exact `getMessage()` result from your exception,
- `{context.traceId}` - the `context.getTraceId()` result for tracking error response with the actual request. The `context` is
  something that is build in `@RestControllerAdvice`s and it contains processing metadata. Currently only `traceId` is
  supported,
- `{fieldName}` - any field name declared in exceptions and its superclasses (scanned from current class to its most
  outer one).

```java
/**
 * <pre>{@code
 * {
 *   "type": "https://example.org/errors/invalid-request",
 *   "title": "Invalid Request",
 *   "status": 400,
 *   "detail": "bad input for user 123: email",
 *   "instance": "https://example.org/instances/trace-789",
 *   "userId": "123",
 *   "fieldName": "email"
 * }
 * }</pre>
 */
@ProblemMapping(
    type = "https://example.org/errors/invalid-request",
    title = "Invalid Request",
    status = 400,
    detail = "{message}: {fieldName}",
    instance = "https://example.org/instances/{context.traceId}",
    extensions = {"userId", "fieldName"})
public class ExampleException extends RuntimeException {

  private final String userId;
  private final String fieldName;

  public ExampleException(String userId, String fieldName) {
    super("bad input for user " + userId);
    this.userId = userId;
    this.fieldName = fieldName;
  }
}
```

**Note** that `@ProblemMapping` is inherited in subclasses so it's possible to rely on it for building exception classes
hierarchy.

#### Implementing `ProblemResolver`

For exceptions, you can't modify, the primary way to integrate with Problem4J to create custom `ProblemResolver`
and declare it as `@Component`.

`ProblemResolver` is an interface used by Problem4J's build-in `@RestControllerAdvice`-s that return `Problem` objects
in response entity. After declaring it as a component for dependency injection, it will be loaded into
`ProblemResolverStore`.

```java
@Component
public class ExampleExceptionResolver implements ProblemResolver {

  @Override
  public Class<? extends Exception> getExceptionClass() {
    return ExampleException.class;
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ExampleException e = (ExampleException) ex;
    return Problem.builder()
        .type("https://example.org/errors/invalid-request")
        .title("Invalid Request")
        .status(400)
        .detail("bad input for user " + e.getUserId())
        .instance("https://example.org/instances/" + context.getTraceId())
        .extension("userId", e.getUserId())
        .extension("fieldName", e.getFieldName());
  }
}
```

You can also override existing `ProblemResolver` implementations to extend models provided by this module. Build-in
resolvers come with `@ConditionalOnMissingBean`, so they can be shadowed by custom ones in target applications.

`ProblemResolver` implementations return a `ProblemBuilder` for flexibility in constructing the final `Problem` object.
It's a convenience method for further extending `Problem` object by processing downstream.

#### Custom `@RestControllerAdvice`

While creating your own `@RestControllerAdvice`, make sure to position it with right `@Order`. In order for your custom
implementation to work seamlessly, make sure to position it on at least **`Ordered.LOWEST_PRECEDENCE - 11`** (the lower
the value, the higher the priority). All `@RestControllerAdvice` provided by `problem4j-spring` library use ordering
from `Ordered.LOWEST_PRECEDENCE` to `Ordered.LOWEST_PRECEDENCE - 10`.

If you want your advice to override the ones provided by this library, use a smaller order value (e.g.
`Ordered.LOWEST_PRECEDENCE - 11` or `Ordered.HIGHEST_PRECEDENCE` if you really mean it).

| <center>covered exceptions</center>             | <center>`@Order(...)`</center>   |
|-------------------------------------------------|----------------------------------|
| Spring's internal exceptions                    | `Ordered.LOWEST_PRECEDENCE - 10` |
| `ProblemException`                              | `Ordered.LOWEST_PRECEDENCE - 10` |
| `Exception` (fallback for all other exceptions) | `Ordered.LOWEST_PRECEDENCE`      |

While implementing custom `@ControllerAdvice`, don't forget of calling `ProblemPostProcessor` manually, before returning
`Problem` object. 

```java
@Order(Ordered.LOWEST_PRECEDENCE - 20)
@Component
@RestControllerAdvice
public class ExampleExceptionAdvice {

  private final ProblemPostProcessor problemPostProcessor;

  // constructor

  @ExceptionHandler(ExampleException.class)
  public ResponseEntity<Problem> handleExampleException(ExampleException ex, WebRequest request) {
    ProblemContext context = (ProblemContext) request.getAttribute(PROBLEM_CONTEXT, SCOPE_REQUEST);
    if (context == null) {
      context = ProblemContext.empty();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem =
        Problem.builder()
            .type("https://example.org/errors/invalid-request")
            .title("Invalid Request")
            .status(400)
            .detail(ex.getMessage())
            .instance("https://example.org/instances/" + context.getTraceId())
            .extension("userId", e.getUserId())
            .extension("fieldName", e.getFieldName())
            .build();
    problem = problemPostProcessor.process(context, problem);

    HttpStatus status = ProblemSupport.resolveStatus(problem.getStatus());

    return new ResponseEntity<>(problem, headers, status);
  }
}
```

#### Spring's build-in `@ResponseStatus` annotation

If your exception is annotated with Spring's built-in `@ResponseStatus`, the library will use the specified HTTP status
and reason (if provided) when building the `Problem` response. The `title` field will be set to the standard reason
phrase for the status code, and the `detail` field will be set to the `reason` specified in the annotation. No
interpolation of fields is supported for this annotation (if you need that, consider using `@ProblemMapping` instead).

```java
/**
 * <pre>{@code
 * {
 *   "status": 404,
 *   "title": "Not Found",
 *   "detail": "reason: resource not found"
 * }
 * }</pre>
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "reason: resource not found")
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String resourceId) {
    super("Resource with ID " + resourceId + " not found");
  }
}
```

#### Using `problem4j-core`

If you can't use `problem4j-spring` (or don't want to), but the idea of `Problem` objects is appealing to you, you may
want to consider relying purely on [`problem4j-core`][problem4j-core] and [`problem4j-jackson`][problem4j-jackson]
libraries. You can build any mechanism for resolving exceptions into `Problem` objects yourself, depending on your own
frameworks, requirements or any other policies. See the `README.md` file in each module for more details - each module
is self-explanatory.

### Inspectors for built-in advices

You can observe how exceptions are translated into `Problem` responses by implementing and registering (depending on
your framework) either `AdviceWebFluxInspector` or `AdviceMvcInspector`.

The primary goal of these inspectors is to let developers customize logging in their preferred style, but you can also
use them for other purposes such as metrics collection, auditing, or debugging.

```java
@Component
public class LoggingInspector implements AdviceMvcInspector {

  private static final Logger log = LoggerFactory.getLogger(AdviceMvcLogger.class);

  @Override
  public void inspect(
      ProblemContext context,
      Problem problem,
      Exception ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) { // AdviceWebFluxInspector declares ServerWebExchange argument
    log.info(
        "Handled [status={} title={}]: exception={}",
        status.value(),
        problem.getTitle(),
        ex.getClass().getSimpleName());
  }
}
```

You can define any number of inspectors, all of them are executed sequentially during exception handling.

### Validation

Library overrides default responses for `jakarta.validation` exceptions for both `@RequestBody` and any other
`@RestController` arguments.

```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "Validation failed",
  "errors": [ {
    "field": "email",
    "error": "must be a well-formed email address"
  }, {
    "field": "age",
    "error": "must be greater than or equal to 18"
  } ]
}
```

More notably, for `@RequestParam`, `@RequestHeader` etc., there's a tweak that comes from settings configuration
property `spring.validation.method.adapt-constraint-violations` to `true`. Enabling it, switches default validation to
not rely on raw `ConstraintViolationException`, but rather on `MethodValidationException`, which contains more details
about validated element.

Let's say we have following `@RestController`, where `customerId` query param has different Java parameter name (its
`String customerIdParam`). We would like to have `customerId` in our response body as potential API clients do not have
knowledge about internal technologies used by backend.

```java
@Validated
@RestController
static class RequestParamController {
  @GetMapping("/orders")
  String endpoint(@RequestParam("customerId") @Size(min = 5, max = 30) String customerIdParam) {
    return "OK";
  }
}
```

The `.errors[].field` will differ, depending on whether `spring.validation.method.adapt-constraint-violations` is
enabled or not. For `true` it will use value from `@RequestParam` (if able), and not from Java method argument name (the
same goes for `@PathVariable`, `@RequestHeader`, `@CookieValue` etc.).

<table>
<tr>
<td align="center"><code>ConstraintViolationException</code></td>
<td align="center"><code>MethodValidationException</code></td>
</tr>
<tr>
<td><pre lang="json">
{
  "status": 400,
  "title": "Bad Request",
  "detail": "Validation failed",
  "errors": [ {
    "field": "customerIdParam",
    "error": "size must be between 5 and 30"
  } ]
}
</pre></td>
<td><pre lang="json">
{
  "status": 400,
  "title": "Bad Request",
  "detail": "Validation failed",
  "errors": [ {
    "field": "customerId",
    "error": "size must be between 5 and 30"
  } ]
}
</pre></td>
</tr>
</table>

[`MethodValidationResolver`][MethodValidationResolver] contains implementation of retrieving configured values from
parameter annotations.

For Spring Boot versions lower than `3.5`, the above-mentioned property is not available and one must configure it
programmatically. Consider checking up `org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration`
and [Method Validation Exceptions][method-validation-exceptions] chapter of Spring Framework documentation.

Example on how to enable it directly is below.

```java
@Configuration
public class ApplicationConfiguration {
  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
    processor.setAdaptConstraintViolations(true);
    return processor;
  }
}
```

Method `setAdaptConstraintViolations` is available since Spring Framework `6.1` (therefore since Spring Boot `3.2`).

### Occurrences of `TypeMismatchException`

Triggered for example when trying to pass `String` value into `@RequestParam("param") Integer param`.

```json
{
    "status": 400,
    "title": "Bad Request",
    "detail": "Type mismatch",
    "property": "age",
    "kind": "integer"
}
```

### Occurrences of `ErrorResponseException`

Similar to `ProblemException`, but comes from Spring and relies on mutable `ProblemDetails` object.

Explicitly thrown `ErrorResponseException` (or subclasses like `ResponseStatusException`). Each of these exceptions
carry HTTP status within it as well as details to be used in `application/problem+json` response.

Example:

```json
{
    "type": "https://example.org/problem-type",
    "title": "Some Error",
    "status": 400,
    "detail": "Explanation of the error",
    "instance": "https://example.org/instances/123"
}
```

### General HTTP Stuff

1. If trying to call `POST` for and endpoint with only `GET` (or any other similar situation), service will write
   following response.
   ```json
   {
       "status": 405,
       "title": "Method Not Allowed"
   }
   ```
2. If calling REST API with invalid `Accept` header, service will write following response.
   ```json
   {
       "status": 406,
       "title": "Not Acceptable"
   }
   ```
3. If calling REST API with invalid `Content-Type` header, service will write following response.
   ```json
   {
       "status": 415,
       "title": "Unsupported Media Type"
   }
   ```
4. If passing request body that has invalid JSON syntax, service will write following response.
   ```json
   {
       "status": 400,
       "title": "Bad Request"
   }
   ```
5. If passing request that's too large by configuration, service will write following response. Note that reason phrase
   for `413` was changed into `Content Too Large` in [RFC 9110 §15.5.14][rfc9110-15.5.4].
   ```json
   {
       "status": 413,
       "title": "Content Too Large"
   }
   ```

## Experimental Features

Problem4J includes a set of **experimental features** designed to explore advanced integration scenarios and enable more
flexible error response customization. These features are stable enough for practical use but may evolve in future
releases as their design matures and community feedback is incorporated.

All experimental features are **opt-in** - they are disabled by default and must be explicitly configured. Use them when
your deployment requires fine-grained control over how `Problem` responses are post-processed or formatted.

### Overriding Problem Fields

Problem4J provides an **experimental post-processing mechanism** that allows modifying certain fields of a `Problem` object
after it has been constructed. This feature makes it possible to generate environment-dependent or runtime-resolved URIs
for fields such as `"type"` and `"instance"`, without embedding such logic into exception classes or resolvers.

Currently, the following fields can be overridden:

- `type` - the logical category of the problem
- `instance` - an identifier of a specific occurrence, often a URI or trace reference

These overrides are applied by a **global post-processor** using templates defined in configuration properties.

#### Placeholders

Templates may include placeholders that are dynamically replaced at runtime.

Available placeholders include:

- for overriding `"type"` field:
    - `{problem.type}` - the original `"type"` value of the problem
- for overriding "instance" field:
    - `{problem.instance}` - the original `"instance"` value of the problem
    - `{context.traceId}` - the trace identifier from the current request (if tracing is enabled)

General post-processing rules:

- Overrides are applied **only if all placeholders in the template can be resolved**:
    - `{problem.type}` - applied if the original `type` is **non-null**, **non-empty**, not `"about:blank"`.
    - `{problem.instance}` - applied if the original `instance` is **non-null** and **non-empty**.
    - `{context.traceId}` - applied if the context provides a **non-null**, **non-empty** trace ID.
- If any referenced placeholder cannot be resolved, **the override is skipped** (occurrences of unknown placeholders
  also abort the override for that field).
- The resulting values are **non-empty strings** and treated as valid URIs.
- If no override is set, fields remain as in the original `Problem`.
- **Static templates** (no placeholders) are always applied, regardless of the original value.

These rules ensure that field transformation is safe and predictable while allowing flexible runtime substitution.

#### Example

If your configuration includes:

```properties
problem4j.type-override=https://errors.example.com/{problem.type}
problem4j.instance-override=/errors/{context.traceId}
```

and a request produces a problem with:

- `type=problems/validation`
- `traceId=WQ1tbs12rtSD`

the resulting response will contain:

- `"type": "https://errors.example.com/problems/validation"`
- `"instance": "/errors/WQ1tbs12rtSD"`

This allows uniform and resolvable links for problem reports across environments.

## Configuration

Library can be configured with following properties.

### `problem4j.detail-format`

Property that specifies how exception handling imported with this module should print the `"detail"` field of the
`Problem` model (`lowercase`, **`capitalized` - default**, `uppercase`). Useful for keeping a consistent style between
errors generated by the library and those from your application.

### `problem4j.tracing-header-name`

Property that specifies the name of the HTTP header used for tracing requests. If set, the trace identifier from this
header is extracted and made available within the request context (`ProblemContext`). This value can be referenced in
other configuration properties using the `{context.traceId}` placeholder. Defaults to `null` (disabled).

### `problem4j.type-override`

Defines a template for overriding the `"type"` field of `Problem` responses. Useful for mapping logical problem
identifiers to environment-specific URIs (for example, production vs. staging). Defaults to `null` (disabled).

See [Overriding Problem Fields](#overriding-problem-fields) chapter for more info.

### `problem4j.instance-override`

Defines a template for overriding the `"instance"` field of `Problem` responses. Useful for appending runtime context
such as request trace identifiers or constructing predictable instance URIs. Defaults to `null` (disabled).

See [Overriding Problem Fields](#overriding-problem-fields) chapter for more info.

### `problem4j.resolver-caching.enabled`

Enables caching of resolved `ProblemResolver` instances to avoid repeated reflection and lookup. Defaults to `false`
(disabled). When disabled, every resolution performs a fresh lookup. Enable if you have many repeated resolutions of a
stable set of exception / resolver types.

### `problem4j.resolver-caching.max-cache-size`

Maximum number of resolver entries stored when caching is enabled. Defaults to `128`. Uses LRU (least recently used)
eviction once the limit is exceeded. Values `<= 0` mean the cache is unbounded (no eviction) - use cautiously if many
distinct resolver types may appear.

Example:

```properties
problem4j.resolver-caching.enabled=true
problem4j.resolver-caching.max-cache-size=256
```

Notes:

- If you rarely introduce new resolver types, a small cache (64-256) is usually enough.
- Leave disabled if startup / reflection cost is negligible or resolver set is highly dynamic.

## FAQ

### Accessing unregistered HTTP path doesn't return proper response body

1. In Spring Boot versions before `3.2.0`, Spring WebMVC required setting following property for
   `NoHandlerFoundException` to ever be thrown.
   ```properties
   spring.mvc.throw-exception-if-no-handler-found=true
   ```
   See `org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties` class to debug it yourself.
2. By default, Spring Boot includes mappings to static resources. If you want to disable them and make Spring return 404
   on `src/main/resources/static/*` (and others), set following property.
   ```properties
   spring.web.resources.add-mappings=false
   ```
   See `org.springframework.boot.autoconfigure.web.SpringWebProperties` class to debug it yourself.

### Messages of `jakarta.validation` errors are localized

Property `spring.web.locale-resolved` default has `accept_header`, to prioritize `Accept` header. Consider updating it
as it follows.

```properties
spring.web.locale=en_US
spring.web.locale-resolver=fixed
```

See `org.springframework.boot.autoconfigure.web.SpringWebProperties` class to debug it yourself.

## Problem4J Links

- [`problem4j-core`][problem4j-core] - Core library defining `Problem` model and `ProblemException`.
- [`problem4j-jackson`][problem4j-jackson] - Jackson module for serializing and deserializing `Problem` objects.
- [`problem4j-spring`][problem4j-spring] - Spring modules extending `ResponseEntityExceptionHandler` for handling
  exceptions and returning `Problem` responses.

[maven-central]: https://central.sonatype.com/namespace/io.github.malczuuu.problem4j

[problem4j-core]: https://github.com/malczuuu/problem4j-core

[problem4j-spring]: https://github.com/malczuuu/problem4j-spring

[problem4j-spring-webflux-readme]: problem4j-spring-webflux/README.md

[problem4j-spring-webmvc-readme]: problem4j-spring-webmvc/README.md

[problem4j-jackson]: https://github.com/malczuuu/problem4j-jackson

[rfc7807]: https://datatracker.ietf.org/doc/html/rfc7807

[rfc9110-15.5.4]: https://datatracker.ietf.org/doc/html/rfc9110#section-15.5.14

[problem4j-core]: https://github.com/malczuuu/problem4j-core

[ProblemResolver]: problem4j-spring-web/src/main/java/io/github/malczuuu/problem4j/spring/web/resolver/ProblemResolver.java

[ProblemResolverConfiguration]: problem4j-spring-web/src/main/java/io/github/malczuuu/problem4j/spring/web/resolver/ProblemResolverConfiguration.java

[MethodValidationResolver]: problem4j-spring-web/src/main/java/io/github/malczuuu/problem4j/spring/web/resolver/MethodValidationResolver.java

[method-validation-exceptions]: https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html#validation-beanvalidation-spring-method-exceptions
