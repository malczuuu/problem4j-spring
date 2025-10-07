package io.github.malczuuu.problem4j.spring.webmvc.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.resolver.AbstractProblemResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.servlet.resource.NoResourceFoundException;

public class NoResourceFoundResolver extends AbstractProblemResolver {

  public NoResourceFoundResolver(ProblemFormat problemFormat) {
    super(NoResourceFoundException.class, problemFormat);
  }

  @Override
  public ProblemBuilder resolve(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.NOT_FOUND);
  }
}
