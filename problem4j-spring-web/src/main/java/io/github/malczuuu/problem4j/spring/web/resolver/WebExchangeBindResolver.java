package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.internal.ViolationResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * Handles {@link WebExchangeBindException} thrown when binding and validation of request data in a
 * WebFlux application fails.
 *
 * <p>This typically occurs when request parameters, path variables, or body content cannot be bound
 * to a target object or violate validation constraints.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 400 (Bad Request) response, often
 * including details about which fields failed binding or validation.
 */
public class WebExchangeBindResolver extends AbstractProblemResolver {

  private final ViolationResolver violationResolver;

  public WebExchangeBindResolver(ProblemFormat problemFormat) {
    super(WebExchangeBindException.class, problemFormat);
    violationResolver = new ViolationResolver(problemFormat);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    WebExchangeBindException e = (WebExchangeBindException) ex;
    return violationResolver.from(e.getBindingResult()).status(ProblemStatus.BAD_REQUEST);
  }
}
