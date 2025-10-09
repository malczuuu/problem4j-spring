package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.resolveStatus;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.internal.ViolationResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

public class HandlerMethodValidationResolver extends AbstractProblemResolver {

  private final ViolationResolver violationResolver;

  public HandlerMethodValidationResolver(ProblemFormat problemFormat) {
    super(HandlerMethodValidationException.class, problemFormat);
    violationResolver = new ViolationResolver(problemFormat);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    HandlerMethodValidationException e = (HandlerMethodValidationException) ex;
    if (status.is5xxServerError()) {
      return Problem.builder().status(resolveStatus(status));
    }
    return violationResolver.from(e).status(resolveStatus(status));
  }
}
