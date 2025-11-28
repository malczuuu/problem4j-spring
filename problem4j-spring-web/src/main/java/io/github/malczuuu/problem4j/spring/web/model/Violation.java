package io.github.malczuuu.problem4j.spring.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import org.springframework.lang.Nullable;

/**
 * Represents a validation violation with a specific field and its corresponding error message.
 *
 * <p>This class is immutable and thread-safe.
 */
public class Violation implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private final @Nullable String field;
  private final @Nullable String error;

  /**
   * Creates a new violation.
   *
   * @param field the name of the field that caused the violation
   * @param error the description of the error
   */
  @JsonCreator
  public Violation(
      @Nullable @JsonProperty("field") String field,
      @Nullable @JsonProperty("error") String error) {
    this.field = field;
    this.error = error;
  }

  /**
   * Returns the field name associated with this violation.
   *
   * @return the field name
   */
  @JsonProperty("field")
  public @Nullable String getField() {
    return field;
  }

  /**
   * Returns the error message associated with this violation.
   *
   * @return the error message
   */
  @JsonProperty("error")
  public @Nullable String getError() {
    return error;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Violation that)) {
      return false;
    }
    return Objects.equals(getField(), that.getField())
        && Objects.equals(getError(), that.getError());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getField(), getError());
  }

  @Override
  public String toString() {
    return "Violation{field='" + getField() + "', error='" + getError() + "'}";
  }
}
