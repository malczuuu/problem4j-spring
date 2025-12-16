package io.github.malczuuu.problem4j.spring.webflux.app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.BindParam;

public record QueryBindRecord(
    @NotNull @Size(min = 1, max = 5) String text,
    @BindParam("num") @NotNull @Positive Integer number) {}
