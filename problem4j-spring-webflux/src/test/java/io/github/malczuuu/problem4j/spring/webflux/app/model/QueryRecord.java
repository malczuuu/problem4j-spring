package io.github.malczuuu.problem4j.spring.webflux.app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record QueryRecord(
    @NotNull @Size(min = 1, max = 5) String text, @NotNull @Positive Integer number) {}
