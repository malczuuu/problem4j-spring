package io.github.malczuuu.problem4j.spring.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a validation violation with a specific field and its corresponding error message.
 *
 * <p>This class is immutable and thread-safe.
 */
public class Violation implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private final String field;
  private final String error;

  /**
   * Creates a new violation.
   *
   * @param field the name of the field that caused the violation
   * @param error the description of the error
   */
  @JsonCreator
  public Violation(@JsonProperty("field") String field, @JsonProperty("error") String error) {
    this.field = field;
    this.error = error;
  }

  /**
   * Returns the field name associated with this violation.
   *
   * @return the field name
   */
  @JsonProperty("field")
  public String getField() {
    return field;
  }

  /**
   * Returns the error message associated with this violation.
   *
   * @return the error message
   */
  @JsonProperty("error")
  public String getError() {
    return error;
  }
}
