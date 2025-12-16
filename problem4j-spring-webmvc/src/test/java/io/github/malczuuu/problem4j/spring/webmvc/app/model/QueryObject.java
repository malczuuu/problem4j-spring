package io.github.malczuuu.problem4j.spring.webmvc.app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class QueryObject {

  @NotNull
  @Size(min = 1, max = 5)
  private String text;

  @NotNull @Positive private Integer number;

  public QueryObject() {}

  public String getText() {
    return text;
  }

  public Integer getNumber() {
    return number;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }
}
