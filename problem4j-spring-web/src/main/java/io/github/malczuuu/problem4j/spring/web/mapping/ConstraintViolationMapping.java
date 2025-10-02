package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
import io.github.malczuuu.problem4j.spring.web.format.PropertyNameFormat;
import io.github.malczuuu.problem4j.spring.web.model.Violation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * Maps {@link ConstraintViolationException} thrown during validation.
 *
 * <p>Transforms validation constraint violations into a {@link Problem} object.
 *
 * <p>Behavior:
 *
 * <ul>
 *   <li>Extracts property paths and violation messages from each {@link ConstraintViolation}.
 *   <li>Formats field names using {@link PropertyNameFormat}.
 *   <li>Formats overall detail message using {@link DetailFormat}.
 *   <li>Includes all validation errors in the {@code errors} extension property of the {@link
 *       Problem} object.
 * </ul>
 */
public class ConstraintViolationMapping implements ExceptionMapping {

  private final DetailFormat detailFormat;
  private final PropertyNameFormat propertyNameFormat;

  public ConstraintViolationMapping(
      DetailFormat detailFormat, PropertyNameFormat propertyNameFormat) {
    this.detailFormat = detailFormat;
    this.propertyNameFormat = propertyNameFormat;
  }

  @Override
  public Class<? extends Exception> getExceptionClass() {
    return ConstraintViolationException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ConstraintViolationException e = (ConstraintViolationException) ex;
    List<Violation> errors =
        e.getConstraintViolations().stream()
            .map(
                violation ->
                    new Violation(
                        propertyNameFormat.format(fetchViolationProperty(violation)),
                        violation.getMessage()))
            .toList();

    return Problem.builder()
        .status(ProblemStatus.BAD_REQUEST)
        .detail(detailFormat.format("Validation failed"))
        .extension("errors", errors)
        .build();
  }

  private String fetchViolationProperty(ConstraintViolation<?> violation) {
    if (violation.getPropertyPath() == null) {
      return "";
    }

    String lastElement = null;
    for (Path.Node node : violation.getPropertyPath()) {
      lastElement = node.getName();
    }

    return lastElement != null ? lastElement : "";
  }
}
