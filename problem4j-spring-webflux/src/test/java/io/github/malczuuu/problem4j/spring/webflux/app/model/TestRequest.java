package io.github.malczuuu.problem4j.spring.webflux.app.model;

import jakarta.validation.constraints.NotBlank;

public record TestRequest(@NotBlank String name, Integer age) {}
