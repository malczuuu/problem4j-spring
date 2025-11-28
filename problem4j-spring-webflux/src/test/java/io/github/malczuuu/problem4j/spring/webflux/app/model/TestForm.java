package io.github.malczuuu.problem4j.spring.webflux.app.model;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public class TestForm {

  @NotNull @Nullable private Integer number;

  public @Nullable Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }
}
