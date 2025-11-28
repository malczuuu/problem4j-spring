package io.github.malczuuu.problem4j.spring.webmvc.app.model;

import org.springframework.lang.Nullable;

@AlwaysInvalid
public record AlwaysInvalidRequest(@Nullable String field) {}
