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

1. What happens if `@PathVariable`, `@RequestParam`, etc. is missing - [`MissingParameterWebFluxTest`][MissingParameterWebFluxTest].
2. What happens if `@PathVariable`, etc. is invalid - [`ValidateParameterWebFluxTest`][ValidateParameterWebFluxTest] (covers both situations for `spring.validation.method.adapt-constraint-violations`).
3. What happens if `@PathVariable`, etc. has incorrect type - [`TypeMismatchWebFluxTest`][TypeMismatchWebFluxTest].
4. What happens if `@RequestPart` is malformed - [`MalformedMultipartWebFluxTest`][MalformedMultipartWebFluxTest].
5. What happens if `@RequestBody` is invalid - [`ValidateRequestBodyWebFluxTest`][ValidateRequestBodyWebFluxTest].
6. What happens if `Accept` is invalid - [`NotAcceptableWebFluxTest`][NotAcceptableWebFluxTest].
7. What happens if `Content-Type` is invalid - [`UnsupportedMediaTypeWebFluxTest`][UnsupportedMediaTypeWebFluxTest].
8. What happens if HTTP method (`GET`,`POST`, etc.) is invalid - [`MethodNotAllowedWebFluxTest`][MethodNotAllowedWebFluxTest].
9. What happens if unknown endpoint (or resource) is accessed - [`NotFoundWebFluxTest`][NotFoundWebFluxTest].
10. What happens if `ErrorResponseException` is thrown - [`ErrorResponseWebFluxTest`][ErrorResponseWebFluxTest].
11. What happens if `ProblemException` is thrown [`ProblemWebFluxAdviceTest`][ProblemWebFluxAdviceTest] (or exception annotated with`@ProblemMapping`).
12. What happens if we enable tracing and `instance-override` [`InstanceOverrideWebFluxTest`][InstanceOverrideWebFluxTest] (it should include `"instance"` field and add tracing header).

[ProblemErrorWebExceptionHandler]: src/main/java/io/github/malczuuu/problem4j/spring/webflux/error/ProblemErrorWebExceptionHandler.java

[ProblemErrorWebFluxConfiguration]: src/main/java/io/github/malczuuu/problem4j/spring/webflux/error/ProblemErrorWebFluxConfiguration.java

[MissingParameterWebFluxTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/MissingParameterWebFluxTest.java

[ValidateParameterWebFluxTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/ValidateParameterWebFluxTest.java

[TypeMismatchWebFluxTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/TypeMismatchWebFluxTest.java

[MalformedMultipartWebFluxTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/MalformedMultipartWebFluxTest.java

[ValidateRequestBodyWebFluxTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/ValidateRequestBodyWebFluxTest.java

[NotAcceptableWebFluxTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/NotAcceptableWebFluxTest.java

[UnsupportedMediaTypeWebFluxTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/UnsupportedMediaTypeWebFluxTest.java

[MethodNotAllowedWebFluxTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/MethodNotAllowedWebFluxTest.java

[NotFoundWebFluxTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/NotFoundWebFluxTest.java

[ErrorResponseWebFluxTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/ErrorResponseWebFluxTest.java

[ProblemWebFluxAdviceTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/ProblemWebFluxAdviceTest.java

[InstanceOverrideWebFluxTest]: src/test/java/io/github/malczuuu/problem4j/spring/webflux/integration/InstanceOverrideWebFluxTest.java
