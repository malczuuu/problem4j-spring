package io.github.malczuuu.problem4j.spring.webflux.app.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;

public record TestRequest(@NotBlank @Nullable String name, @Nullable Integer age) {}
