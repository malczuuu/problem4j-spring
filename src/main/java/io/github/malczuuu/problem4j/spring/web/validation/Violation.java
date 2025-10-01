package io.github.malczuuu.problem4j.spring.web.validation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;

public class Violation implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private final String field;
  private final String error;

  @JsonCreator
  public Violation(@JsonProperty("field") String field, @JsonProperty("error") String error) {
    this.field = field;
    this.error = error;
  }

  @JsonProperty("field")
  public String getField() {
    return field;
  }

  @JsonProperty("error")
  public String getError() {
    return error;
  }
}
