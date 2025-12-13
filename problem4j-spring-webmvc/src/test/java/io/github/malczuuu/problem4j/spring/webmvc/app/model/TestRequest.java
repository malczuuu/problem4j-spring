package io.github.malczuuu.problem4j.spring.webmvc.app.model;

import jakarta.validation.constraints.NotBlank;

public record TestRequest(@NotBlank String name, Integer age) {}
