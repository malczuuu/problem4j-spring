package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handles {@link ResponseStatusException} thrown to signal a specific HTTP status and optional
 * reason from application code or framework components.
 *
 * <p>This exception can be thrown directly in controllers, services, or other layers to indicate
 * errors such as 404 (Not Found), 403 (Forbidden), or 500 (Internal Server Error) without relying
 * on checked exceptions or custom error types.
 *
 * <p>The handler is responsible for translating the exception into the corresponding HTTP response
 * with the specified status code, reason, and any additional details.
 */
public class ResponseStatusResolver extends AbstractProblemResolver {

  public ResponseStatusResolver(ProblemFormat problemFormat) {
    super(ResponseStatusException.class, problemFormat);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ResponseStatusException e = (ResponseStatusException) ex;
    return Problem.builder()
        .status(
            ProblemStatus.findValue(e.getStatusCode().value())
                .orElse(ProblemStatus.INTERNAL_SERVER_ERROR));
  }
}
