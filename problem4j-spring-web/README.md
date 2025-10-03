# Built-in Spring Exception Mappings

This module overrides Spring Web's default (often minimal or plain-text) responses for many framework exceptions and
produces structured RFC 7807 `Problem` objects. [`ExceptionMappingConfiguration`][ExceptionMappingConfiguration]
registers these mappings.

These error mappings to disallow leaking too much information to the client application. It more information is
necessary, feel free to override specific [`ExceptionMapping`][ExceptionMapping], register it as `@Serivce`,
`@Component` or `@Bean` and exclude specific nested[`ExceptionMappingConfiguration`][ExceptionMappingConfiguration]
configuration class with `@ConditionalOnClass`, per appropriate exception. Therefore, if using this library with
previous versions, mappings for exception classes that are not present in classpath are silently ignored.

Overriding whole `ProblemEnhancedExceptionHandler` is not recommended, although such necessities are sometimes
understandable.

## Occurrences of `ProblemException`

Explicitly thrown `ProblemException` (or subclasses created by its users). Each of these exceptions carry HTTP status
within it as well as details to be used in `application/problem+json` response.

```json
{
  "type": "https://example.org/problem-type",
  "title": "Some Error",
  "status": 400,
  "detail": "Explanation of the error",
  "instance": "https://example.org/instances/123"
}
```

**Note** that the main reason behind this project is to make `ProblemException` a base class for all custom exception in
your application code.

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

Let's say we have following `@RestController`, where `idx` query param has different Java parameter name.

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

The `errors.$.field` will differ, depending on whether `spring.validation.method.adapt-constraint-violations` is enabled
or not. For `true` it will use value from `@RequestParam` (if able) (the same goes for `@PathVariable`,
`@RequestHeader`, `@CookieValue` etc.).

<table>
<tr>
<td style="text-align:center"><code>ConstraintViolationException</code></td>
<td style="text-align:center"><code>MethodValidationException</code></td>
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

[rfc9110-15.5.4]: https://datatracker.ietf.org/doc/html/rfc9110#section-15.5.14

[ExceptionMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/ExceptionMapping.java

[ExceptionMappingConfiguration]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/ExceptionMappingConfiguration.java
