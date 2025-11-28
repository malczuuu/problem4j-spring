package io.github.malczuuu.problem4j.spring.webmvc.app.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;

public record TestRequest(@NotBlank @Nullable String name, @Nullable Integer age) {}
