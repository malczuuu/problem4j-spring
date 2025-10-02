# Built-in Spring Exception Mappings

This module extends `problem4j-spring-web` overrides of responses for many framework exceptions and produces structured
RFC 7807 `Problem` objects, with exceptions that are specific to `spring-webmvc`.

## `NoHandlerFoundException`

What triggers it: DispatcherServlet could not find any handler (no matching controller) for the request (requires
`throwExceptionIfNoHandlerFound=true`).

Mapping: [`NoHandlerFoundMapping`][NoHandlerFoundMapping]

```json
{
  "status": 404,
  "title": "Not Found"
}
```

## `NoResourceFoundException`

What triggers it: Static resource handling (e.g. `ResourceHttpRequestHandler`) couldn't resolve the requested resource (
Spring Boot 3.x when resource chain handling is enabled).

Mapping: [`NoResourceFoundMapping`][NoResourceFoundMapping]

```json
{
  "status": 404,
  "title": "Not Found"
}
```

[NoHandlerFoundMapping]: src/main/java/io/github/malczuuu/problem4j/spring/webmvc/mapping/NoHandlerFoundMapping.java

[NoResourceFoundMapping]: src/main/java/io/github/malczuuu/problem4j/spring/webmvc/mapping/NoResourceFoundMapping.java
