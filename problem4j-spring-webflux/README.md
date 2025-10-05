# Overrides for Spring WebFlux

This module extends `problem4j-spring-web` overrides of responses for many framework exceptions and produces structured
RFC 7807 `Problem` objects, with exceptions that are specific to `spring-webflux`.

## Override `ProblemErrorWebExceptionHandler`

[`ProblemErrorWebExceptionHandler`][ProblemErrorWebExceptionHandler] overrides default error fallback for
`spring-webflux`. Default one distinguishes between `Accept` header to display a formatted page or JSON with build-in
`ErrorAttributes`.

```json
{
  "timestamp": 1426615606,
  "exception": "org.springframework.web.bind.MissingServletRequestParameterException",
  "status": 400,
  "error": "Bad Request",
  "path": "/welcome",
  "message": "Required String parameter 'name' is not present"
}
```

- It can be overwritten by declaring a custom `ErrorWebExceptionHandler` component.
- Exclude [`ProblemErrorWebFluxConfiguration`][ProblemErrorWebFluxConfiguration] do disable this override.

## Main Test Scenarios

1. What happens if `@PathVariable`, `@RequestParam`, etc. is missing - [`MissingParameterTest`][MissingParameterTest].
2. What happens if `@PathVariable`, etc. is invalid - [`ValidateParameterTest`][ValidateParameterTest] (covers both
   situations for `spring.validation.method.adapt-constraint-violations`).
3. What happens if `@PathVariable`, etc. has incorrect type - [`TypeMismatchTest`][TypeMismatchTest].
4. What happens if `@RequestPart` is malformed - [`MalformedMultipartTest`][MalformedMultipartTest].
5. What happens if `@RequestBody` is invalid - [`ValidateRequestBodyTest`][ValidateRequestBodyTest].
6. What happens if `Accept` is invalid - [`NotAcceptableTest`][NotAcceptableTest].
7. What happens if `Content-Type` is invalid - [`UnsupportedMediaTypeTest`][UnsupportedMediaTypeTest].
8. What happens if HTTP method (`GET`,`POST`, etc.) is invalid - [`MethodNotAllowedTest`][MethodNotAllowedTest].
9. What happens if unknown endpoint (or resource) is accessed - [`NotFoundTest`][NotFoundTest].
10. What happens if `ErrorResponseException` is thrown - [`ErrorResponseTest`][ErrorResponseTest].
11. What happens if `ProblemException` is thrown [`ProblemFluxAdviceTest`][ProblemFluxAdviceTest] (or exception
    annotated with`@ProblemMapping`).
12. What happens if we enable tracing and `instance-override` [`InstanceOverrideTest`][InstanceOverrideTest] (it should
    include `"instance"` field and add tracing header.

[ProblemErrorWebExceptionHandler]: src/main/java/io/github/malczuuu/problem4j/spring/webflux/error/ProblemErrorWebExceptionHandler.java

[ProblemErrorWebFluxConfiguration]: src/main/java/io/github/malczuuu/problem4j/spring/webflux/error/ProblemErrorWebFluxConfiguration.java


[MissingParameterTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/MissingParameterTest.java

[ValidateParameterTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/ValidateParameterTest.java

[TypeMismatchTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/TypeMismatchTest.java

[MalformedMultipartTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/MalformedMultipartTest.java

[ValidateRequestBodyTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/ValidateRequestBodyTest.java

[NotAcceptableTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/NotAcceptableTest.java

[UnsupportedMediaTypeTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/UnsupportedMediaTypeTest.java

[MethodNotAllowedTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/MethodNotAllowedTest.java

[NotFoundTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/NotFoundTest.java

[ErrorResponseTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/ErrorResponseTest.java

[ProblemFluxAdviceTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/ProblemFluxAdviceTest.java

[InstanceOverrideTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/InstanceOverrideTest.java
