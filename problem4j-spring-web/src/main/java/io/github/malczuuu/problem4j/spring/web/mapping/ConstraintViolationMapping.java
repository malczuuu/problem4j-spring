package io.github.malczuuu.problem4j.spring.web.mapping;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
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
 *   <li>Includes all validation errors in the {@code errors} extension property of the {@link
 *       Problem} object.
 * </ul>
 */
public class ConstraintViolationMapping extends AbstractExceptionMapping {

  public ConstraintViolationMapping(ProblemFormat problemFormat) {
    super(ConstraintViolationException.class, problemFormat);
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ConstraintViolationException e = (ConstraintViolationException) ex;
    List<Violation> errors =
        e.getConstraintViolations().stream()
            .map(
                violation ->
                    new Violation(fetchViolationProperty(violation), violation.getMessage()))
            .toList();

    return Problem.builder()
        .status(ProblemStatus.BAD_REQUEST)
        .detail(formatDetail(VALIDATION_FAILED_DETAIL))
        .extension(ERRORS_EXTENSION, errors)
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
