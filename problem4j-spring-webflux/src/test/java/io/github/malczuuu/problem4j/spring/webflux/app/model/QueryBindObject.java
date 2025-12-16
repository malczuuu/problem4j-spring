package io.github.malczuuu.problem4j.spring.webflux.app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.BindParam;

public class QueryBindObject {

  @NotNull
  @Size(min = 1, max = 5)
  private final String text;

  @NotNull @Positive private final Integer number;

  public QueryBindObject(String text, @BindParam("num") Integer number) {
    this.text = text;
    this.number = number;
  }

  public String getText() {
    return text;
  }

  public Integer getNumber() {
    return number;
  }
}
