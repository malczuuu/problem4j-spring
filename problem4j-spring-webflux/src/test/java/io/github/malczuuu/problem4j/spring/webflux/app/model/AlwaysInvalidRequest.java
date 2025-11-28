package io.github.malczuuu.problem4j.spring.webflux.app.model;

import org.springframework.lang.Nullable;

@AlwaysInvalid
public record AlwaysInvalidRequest(@Nullable String field) {}
