# Overrides for Spring WebMVC

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

## Override `ProblemErrorController`

[`ProblemErrorController`][ProblemErrorController] overrides default error fallback for `spring-webmvc`. Default one
distinguishes between `Accept` header to display a formatted page or JSON with build-in `ErrorAttributes`.

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

- It can be overwritten by declaring a custom `ErrorController` component.
- Exclude [`ProblemErrorMvcConfiguration`][ProblemErrorMvcConfiguration] do disable this override.

## Main Test Scenarios

1. What happens if `@PathVariable`, `@RequestParam`, etc. is missing - [`MissingParameterMvcTest`][MissingParameterMvcTest].
2. What happens if `@PathVariable`, etc. is invalid - [`ValidateParameterMvcTest`][ValidateParameterMvcTest] (covers both situations for `spring.validation.method.adapt-constraint-violations`).
3. What happens if `@PathVariable`, etc. has incorrect type - [`TypeMismatchMvcTest`][TypeMismatchMvcTest].
4. What happens if `@RequestPart` is malformed - [`MalformedMultipartMvcTest`][MalformedMultipartMvcTest].
5. What happens if `@RequestBody` is invalid - [`ValidateRequestBodyMvcTest`][ValidateRequestBodyMvcTest].
6. What happens if `Accept` is invalid - [`NotAcceptableMvcTest`][NotAcceptableMvcTest].
7. What happens if `Content-Type` is invalid - [`UnsupportedMediaTypeMvcTest`][UnsupportedMediaTypeMvcTest].
8. What happens if HTTP method (`GET`,`POST`, etc.) is invalid - [`MethodNotAllowedMvcTest`][MethodNotAllowedMvcTest].
9. What happens if unknown endpoint (or resource) is accessed - [`NotFoundMvcTest`][NotFoundMvcTest].
10. What happens if `ErrorResponseException` is thrown - [`ErrorResponseMvcTest`][ErrorResponseMvcTest].
11. What happens if `ProblemException` is thrown [`ProblemMvcAdviceTest`][ProblemMvcAdviceTest] (or exception annotated with`@ProblemMapping`).
12. What happens if we enable tracing and `instance-override` [`InstanceOverrideMvcTest`][InstanceOverrideMvcTest] (it should include `"instance"` field and add tracing header).

[ProblemErrorController]: src/main/java/io/github/malczuuu/problem4j/spring/webmvc/error/ProblemErrorController.java

[ProblemErrorMvcConfiguration]: src/main/java/io/github/malczuuu/problem4j/spring/webmvc/error/ProblemErrorMvcConfiguration.java

[MissingParameterMvcTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/MissingParameterMvcTest.java

[ValidateParameterMvcTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/ValidateParameterMvcTest.java

[TypeMismatchMvcTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/TypeMismatchMvcTest.java

[MalformedMultipartMvcTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/MalformedMultipartMvcTest.java

[ValidateRequestBodyMvcTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/ValidateRequestBodyMvcTest.java

[NotAcceptableMvcTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/NotAcceptableMvcTest.java

[UnsupportedMediaTypeMvcTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/UnsupportedMediaTypeMvcTest.java

[MethodNotAllowedMvcTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/MethodNotAllowedMvcTest.java

[NotFoundMvcTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/NotFoundMvcTest.java

[ErrorResponseMvcTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/ErrorResponseMvcTest.java

[ProblemMvcAdviceTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/ProblemMvcAdviceTest.java

[InstanceOverrideMvcTest]: src/test/java/io/github/malczuuu/problem4j/spring/webmvc/integration/InstanceOverrideMvcTest.java
