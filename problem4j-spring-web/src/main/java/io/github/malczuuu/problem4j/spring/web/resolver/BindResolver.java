package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.internal.ViolationResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.BindException;

/**
 * Due to {@code BindException} being subclassed by {@code MethodArgumentNotValidException}, this
 * implementation also covers that exceptions.
 *
 * <p>Quite obvious message, but worth to note that the only reason {@code BindResolver} is kept is
 * due to backwards compatibility as {@code problem4j} doesn't use any fields from that subclass at
 * the moment.
 *
 * @see org.springframework.web.bind.MethodArgumentNotValidException
 */
public class BindResolver extends AbstractProblemResolver {

  private final ViolationResolver violationResolver;

  public BindResolver(ProblemFormat problemFormat) {
    super(BindException.class, problemFormat);
    violationResolver = new ViolationResolver(problemFormat);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    BindException e = (BindException) ex;
    return violationResolver.from(e.getBindingResult()).status(ProblemStatus.BAD_REQUEST);
  }
}
