package io.github.malczuuu.problem4j.spring.webmvc.app.model;

import static io.github.malczuuu.problem4j.spring.webmvc.app.model.ModelUtils.safeParseInt;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.BindParam;

public record QueryBindCtorsRecord(
    @NotNull @Size(min = 1, max = 5) String text,
    @BindParam("num") @NotNull @Positive Integer number) {

  public QueryBindCtorsRecord(String text) {
    this(text, safeParseInt(text));
  }
}
