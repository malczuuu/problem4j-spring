# Mapping exceptions to `application/problem+json` responses

1. [Overview](#overview)
2. [Returning response bodies from custom exceptions](#returning-response-bodies-from-custom-exceptions)
    1. [Extending `ProblemException`](#extending-problemexception)
    2. [Annotating `@ProblemMapping`](#annotating-problemmapping)
    3. [Implementing `ProblemResolver`](#implementing-problemresolver) 
    4. [Custom `@RestControllerAdvice` implementation](#custom-restcontrolleradvice) 
3. [Validation](#validation)
4. [Occurrences of `TypeMismatchException`](#occurrences-of-typemismatchexception)
5. [Occurrences of `ErrorResponseException`](#occurrences-of-errorresponseexception)
6. [General HTTP Stuff](#general-http-stuff)
7. [FAQ](#faq)

## Overview

This module overrides Spring Web's default (often minimal or plain-text) responses for many framework exceptions and
produces structured RFC 7807 `Problem` objects. [`ProblemResolverConfiguration`][ProblemResolverConfiguration]
registers these resolvers.

These error resolvers to disallow leaking too much information to the client application. It more information is
necessary, feel free to override specific [`ProblemResolver`][ProblemResolver], register it as `@Serivce`,
`@Component` or `@Bean` and exclude specific nested[`ProblemResolverConfiguration`][ProblemResolverConfiguration]
configuration class with `@ConditionalOnClass`, per appropriate exception. Therefore, if using this library with
previous versions, resolvers for exception classes that are not present in classpath are silently ignored.

Overriding whole `ProblemEnhancedExceptionHandler` is not recommended, although such necessities are sometimes
understandable.

## Returning response bodies from custom exceptions

As mentioned in main [`README.md`](../README.md), you can either extend `ProblemException` or add `@ProblemMapping` to
your exception class.

### Extending `ProblemException`

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

### Annotating `@ProblemMapping`

For exceptions that cannot extend `ProblemException`, you can annotate them with `@ProblemMapping`. This allows you to
declaratively map exception fields to a `Problem`.

To extract values from target exception, it's possible to use placeholders for interpolation.

- `{message}` - the exact `getMessage()` result from your exception,
- `{traceId}` - the `context.getTraceId()` result for tracking error response with the actual request. The `context` is
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
    instance = "https://example.org/instances/{traceId}",
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

### Implementing `ProblemResolver`

For exceptions, you can't modify, the primary way to integrate with Problem4J to create custom `ProblemResolver`
and declare it as `@Component`.

`ProblemResolver` is an interface used by Problem4J's build-in `@RestControllerAdvice`-s that return `Problem` objects
in response entity. After declaring it as a component for dependency injection, it will be loaded into
`ProblemResolverStore`.

```java
@Component
public class MaxUploadSizeExceededResolver implements ProblemResolver {

  @Override
  public Class<? extends Exception> getExceptionClass() {
    return ExampleException.class;
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    MaxUploadSizeExceededException e = (MaxUploadSizeExceededException) ex;
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

`ProblemResolver` implementations return a `ProblemBuilder` for flexibility in constructing the final `Problem` object.
The interface contains `default Problem resolveProblem(...)` method that calls `problemBuilder(...).build()` for
convenience. The `resolveProblem` method should not be overwritten.

### Custom `@RestControllerAdvice`

While creating your own `@RestControllerAdvice`, make sure to position it with right `@Order`. In order for your custom
implementation to work seamlessly, make sure to position it on at least **`Ordered.LOWEST_PRECEDENCE - 11`** (the lower
the value, the higher the priority). All `@RestControllerAdvice` provided by `problem4j-spring` library use ordering
from `Ordered.LOWEST_PRECEDENCE` to `Ordered.LOWEST_PRECEDENCE - 10`. By setting at lest `-11`, you make sure that your
exception will not fall into predefined advices.

But let's be honest, you'll probably use `Ordered.HIGHEST_PRECEDENCE` :D.

| <center>covered exceptions</center> | <center>`@Order(...)`</center>   |
|-------------------------------------|----------------------------------|
| Spring's internal exceptions        | `Ordered.LOWEST_PRECEDENCE - 10` |
| `ConstraintViolationException`      | `Ordered.LOWEST_PRECEDENCE - 10` |
| `DecodingException`                 | `Ordered.LOWEST_PRECEDENCE - 10` |
| `ProblemException`                  | `Ordered.LOWEST_PRECEDENCE - 10` |
| `Exception`                         | `Ordered.LOWEST_PRECEDENCE`      |

While implementing custom `@ControllerAdvice`, enforcing `instance-override` feature must be performed manually. Value
of `"instance"` field will come from request attributes and must be overwritten if not `null`.

```java
@Order(Ordered.LOWEST_PRECEDENCE - 20)
@Component
@RestControllerAdvice
public class ExampleExceptionAdvice {

  @ExceptionHandler(ExampleException.class)
  public ResponseEntity<Problem> handleExampleException(ExampleException ex, WebRequest request) {
    ProblemBuilder builder =
        Problem.builder()
            .type("http://example.org/errors/example-error")
            .title("Example Title")
            .status(400)
            .detail(ex.getMessage())
            .instance("https://example.org/instances/example-instance");

    Object instanceOverride = request.getAttribute(TracingSupport.INSTANCE_OVERRIDE, SCOPE_REQUEST);
    if (instanceOverride != null) {
      builder = builder.instance(instanceOverride.toString());
    }

    Problem problem = builder.build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    HttpStatus status = ProblemSupport.resolveStatus(problem.getStatus());

    return new ResponseEntity<>(problem, headers, status);
  }
}
```

## Validation

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

Creating response body with adapting turned on was implemented in [`MethodValidationResolver`][MethodValidationResolver].

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

## Occurrences of `TypeMismatchException`

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

## Occurrences of `ErrorResponseException`

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

## General HTTP Stuff

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

[rfc9110-15.5.4]: https://datatracker.ietf.org/doc/html/rfc9110#section-15.5.14

[ProblemResolver]: src/main/java/io/github/malczuuu/problem4j/spring/web/resolver/ProblemResolver.java

[ProblemResolverConfiguration]: src/main/java/io/github/malczuuu/problem4j/spring/web/resolver/ProblemResolverConfiguration.java

[MethodValidationResolver]: src/main/java/io/github/malczuuu/problem4j/spring/web/resolver/MethodValidationResolver.java

[method-validation-exceptions]: https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html#validation-beanvalidation-spring-method-exceptions
