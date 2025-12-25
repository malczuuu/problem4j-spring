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

- It can be overwritten further by declaring a custom `ErrorController` component.
- Exclude [`ProblemErrorMvcConfiguration`][ProblemErrorMvcConfiguration] do disable this override.

[ProblemErrorController]: src/main/java/io/github/problem4j/spring/webmvc/ProblemErrorController.java

[ProblemErrorMvcConfiguration]: src/main/java/io/github/problem4j/spring/webmvc/autoconfigure/ProblemErrorMvcConfiguration.java
