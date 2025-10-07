package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponseException;

public class ErrorResponseResolver extends AbstractProblemResolver {

  protected ErrorResponseResolver(ProblemFormat problemFormat) {
    super(ErrorResponseException.class, problemFormat);
  }

  @Override
  public ProblemBuilder resolve(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ErrorResponseException e = (ErrorResponseException) ex;
    ProblemBuilder builder =
        Problem.builder()
            .type(e.getBody().getType())
            .title(e.getBody().getTitle())
            .status(e.getStatusCode().value())
            .detail(e.getBody().getDetail())
            .instance(e.getBody().getInstance());

    if (e.getBody().getProperties() != null) {
      builder = builder.extension(e.getBody().getProperties());
    }

    return builder;
  }
}
