# Built-in Spring Exception Mappings

This module overrides Spring Web's default (often minimal or plain-text) responses for many framework exceptions and
produces structured RFC 7807 `Problem` objects. [`ExceptionMappingConfiguration`][ExceptionMappingConfiguration]
registers these mappings.

These error mappings to disallow leaking too much information to the client application. It more information is
necessary, feel free to override specific [`ExceptionMapping`][ExceptionMapping], register it as `@Serivce`,
`@Component` or `@Bean` and exclude specific nested[`ExceptionMappingConfiguration`][ExceptionMappingConfiguration]
configuration class.

Overriding whole `ProblemEnhancedExceptionHandler` is not recommended, although such necessities are sometimes
understandable.

## `AsyncRequestTimeoutException`

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

## `ConversionNotSupportedException`

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

## `ErrorResponseException`

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

## `HttpMediaTypeNotAcceptableException`

What triggers it: Client `Accept` header doesn't match any producible media type from controller.

Mapping: [`HttpMediaTypeNotAcceptableMapping`][HttpMediaTypeNotAcceptableMapping]

```json
{
  "status": 406,
  "title": "Not Acceptable"
}
```

## `HttpMediaTypeNotSupportedException`

What triggers it: Request `Content-Type` not supported by any `HttpMessageConverter` for the target endpoint.

Mapping: [`HttpMediaTypeNotSupportedMapping`][HttpMediaTypeNotSupportedMapping]

```json
{
  "status": 415,
  "title": "Unsupported Media Type"
}
```

## `HttpMessageNotReadableException`

What triggers it: Incoming request body couldn't be parsed/deserialized (malformed JSON, wrong structure, EOF, etc.).

Mapping: [`HttpMessageNotReadableMapping`][HttpMessageNotReadableMapping]

```json
{
  "status": 400,
  "title": "Bad Request"
}
```

## `HttpMessageNotWritableException`

What triggers it: Server failed to serialize the response body (e.g. Jackson serialization problem) after controller
returned a value.

Mapping: [`HttpMessageNotWritableMapping`][HttpMessageNotWritableMapping]

```json
{
  "status": 500,
  "title": "Internal Server Error"
}
```

## `HttpRequestMethodNotSupportedException`

What triggers it: HTTP method not supported for a particular endpoint (e.g. `POST` to an endpoint allowing only `GET`).

Mapping: [`HttpRequestMethodNotSupportedMapping`][HttpRequestMethodNotSupportedMapping]

```json
{
  "status": 405,
  "title": "Method Not Allowed"
}
```

## `MaxUploadSizeExceededException`

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

## `MethodArgumentNotValidException`

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

## `MissingPathVariableException`

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

## `MissingServletRequestParameterException`

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

## `MissingServletRequestPartException`

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

## `ServletRequestBindingException`

What triggers it: General binding issues with request parameters, headers, path variables (e.g. missing header required
by `@RequestHeader`).

Mapping: [`ServletRequestBindingMapping`][ServletRequestBindingMapping]

```json
{
  "status": 400,
  "title": "Bad Request"
}
```

## `TypeMismatchException`

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

## Fallback / Unknown Exceptions

What triggers it: Any unhandled exception flowing through `ResponseEntityExceptionHandler` without a dedicated mapping.
Result example:

```json
{
  "status": 500,
  "title": "Internal Server Error"
}
```

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

[ServletRequestBindingMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/ServletRequestBindingMapping.java

[TypeMismatchMapping]: src/main/java/io/github/malczuuu/problem4j/spring/web/mapping/TypeMismatchMapping.java
