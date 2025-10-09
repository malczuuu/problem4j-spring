package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.model.Violation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * Handles {@link ConstraintViolationException} thrown when one or more Bean Validation constraints
 * are violated.
 *
 * <p>Relates with {@code MethodValidationException} (see {@link MethodValidationResolver}).
 *
 * <p>This exception indicates that method parameters, return values, or other validated elements
 * failed to satisfy declared {@code @Valid} or {@code @Constraint} annotations.
 */
public class ConstraintViolationResolver extends AbstractProblemResolver {

  public ConstraintViolationResolver(ProblemFormat problemFormat) {
    super(ConstraintViolationException.class, problemFormat);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ConstraintViolationException e = (ConstraintViolationException) ex;
    List<Violation> errors = extractViolations(e);

    return Problem.builder()
        .status(ProblemStatus.BAD_REQUEST)
        .detail(formatDetail(VALIDATION_FAILED_DETAIL))
        .extension(ERRORS_EXTENSION, errors);
  }

  private List<Violation> extractViolations(ConstraintViolationException e) {
    return e.getConstraintViolations().stream()
        .map(violation -> new Violation(fetchViolationProperty(violation), violation.getMessage()))
        .toList();
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
