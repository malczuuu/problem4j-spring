# Built-in Spring Exception Mappings

This module extends `problem4j-spring-web` overrides of responses for many framework exceptions and produces structured
RFC 7807 `Problem` objects, with exceptions that are specific to `spring-webmvc`.

## Override `404 Not Found`

- `NoHandlerFoundException`
- `NoResourceFoundException`

Makes both `404 Not Found` responses exactly the same so information about what is a static resource and what is a
controller never leaks.

```json
{
  "status": 404,
  "title": "Not Found"
}
```

## Main Test Scenarios

1. What happens if `@PathVariable`, `@RequestParam`, etc. is missing - [`MissingParameterTest`][MissingParameterTest]
2. What happens if `@PathVariable`, etc. is invalid - [`ValidateParameterTest`][ValidateParameterTest]
3. What happens if `@PathVariable`, etc. has incorrect type - [`TypeMismatchTest`][TypeMismatchTest]
4. What happens if `@RequestPart` is malformed - [`MalformedMultipartTest`][MalformedMultipartTest]
5. What happens if `@RequestBody` is invalid - [`ValidateRequestBodyTest`][ValidateRequestBodyTest]
6. What happens if `Accept` is invalid - [`NotAcceptableTest`][NotAcceptableTest]
7. What happens if `Content-Type` is invalid - [`UnsupportedMediaTypeTest`][UnsupportedMediaTypeTest]
8. What happens if HTTP method (`GET`,`POST`, etc.) is invalid - [`MethodNotAllowedTest`][MethodNotAllowedTest]
9. What happens if unknown endpoint (or resource) is accessed - [`NotFoundTest`][NotFoundTest]
10. What happens if `ErrorResponseException` is thrown - [`ErrorResponseTest`][ErrorResponseTest]

[MissingParameterTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/MissingParameterTest.java

[ValidateParameterTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/ValidateParameterTest.java

[TypeMismatchTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/TypeMismatchTest.java

[MalformedMultipartTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/MalformedMultipartTest.java

[ValidateRequestBodyTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/ValidateRequestBodyTest.java

[NotAcceptableTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/NotAcceptableTest.java

[UnsupportedMediaTypeTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/UnsupportedMediaTypeTest.java

[MethodNotAllowedTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/MethodNotAllowedTest.java

[NotFoundTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/NotFoundTest.java

[ErrorResponseTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/ErrorResponseTest.java
