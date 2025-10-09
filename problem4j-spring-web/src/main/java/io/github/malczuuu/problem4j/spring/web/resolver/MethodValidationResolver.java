package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.internal.ViolationResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.method.MethodValidationException;

/**
 * Handles {@link MethodValidationException} thrown when method-level Bean Validation fails.
 *
 * <p>This exception is raised for methods annotated with {@code @Validated} or containing
 * {@code @Constraint}-annotated parameters or return values that do not satisfy declared validation
 * rules.
 *
 * <p>When method validation adaptation is enabled (e.g. via {@code @EnableMethodValidation}),
 * Spring intercepts method invocations, delegates to a Bean Validation provider, and wraps any
 * resulting {@code ConstraintViolationException} in a {@link MethodValidationException}.
 *
 * <p>This allows framework components and exception handlers to deal with a consistent,
 * Spring-specific exception type instead of the raw Jakarta exception.
 *
 * @see jakarta.validation.ConstraintViolationException
 */
public class MethodValidationResolver extends AbstractProblemResolver {

  private final ViolationResolver violationResolver;

  public MethodValidationResolver(ProblemFormat problemFormat) {
    super(MethodValidationException.class, problemFormat);
    violationResolver = new ViolationResolver(problemFormat);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    MethodValidationException e = (MethodValidationException) ex;
    return violationResolver.from(e).status(ProblemStatus.BAD_REQUEST);
  }
}
