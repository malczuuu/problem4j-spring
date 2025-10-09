# Overrides for Spring WebFlux

This module extends `problem4j-spring-web` overrides of responses for many framework exceptions and produces structured
RFC 7807 `Problem` objects, with exceptions that are specific to `spring-webflux`.

## Override `ProblemErrorWebExceptionHandler`

[`ProblemErrorWebExceptionHandler`][ProblemErrorWebExceptionHandler] overrides default error fallback for
`spring-webflux`. Default one distinguishes between `Accept` header to display a formatted page or JSON with build-in
`ErrorAttributes`.

- It can be overwritten further by declaring a custom `ErrorWebExceptionHandler` component.
- Exclude [`ProblemErrorWebFluxConfiguration`][ProblemErrorWebFluxConfiguration] do disable this override.

[ProblemErrorWebExceptionHandler]: src/main/java/io/github/malczuuu/problem4j/spring/webflux/error/ProblemErrorWebExceptionHandler.java

[ProblemErrorWebFluxConfiguration]: src/main/java/io/github/malczuuu/problem4j/spring/webflux/error/ProblemErrorWebFluxConfiguration.java
