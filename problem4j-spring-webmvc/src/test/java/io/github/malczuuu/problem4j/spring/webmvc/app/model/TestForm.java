package io.github.malczuuu.problem4j.spring.webmvc.app.model;

import jakarta.validation.constraints.NotNull;

public class TestForm {

  @NotNull private Integer number;

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }
}
