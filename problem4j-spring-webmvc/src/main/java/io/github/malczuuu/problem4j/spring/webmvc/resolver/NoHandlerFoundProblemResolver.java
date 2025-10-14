package io.github.malczuuu.problem4j.spring.webmvc.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.resolver.AbstractProblemResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Handles {@link NoHandlerFoundException} thrown when no matching handler (controller method) is
 * found for a given request.
 *
 * <p>This typically occurs when the client requests a URL or HTTP method that does not correspond
 * to any mapped controller endpoint.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 404 (Not Found) response to
 * indicate that the requested resource or endpoint does not exist.
 */
public class NoHandlerFoundProblemResolver extends AbstractProblemResolver {

  public NoHandlerFoundProblemResolver(ProblemFormat problemFormat) {
    super(NoHandlerFoundException.class, problemFormat);
  }

  /**
   * Returns a {@link ProblemBuilder} with {@link ProblemStatus#NOT_FOUND} (HTTP 404) indicating no
   * controller handler matched the incoming request (URL + HTTP method). Other parameters ({@code
   * context}, {@code headers}, {@code status}) are ignored because the semantics of {@code
   * NoHandlerFoundException} unambiguously map to 404.
   *
   * @param context problem context (unused)
   * @param ex the triggering {@link NoHandlerFoundException}
   * @param headers HTTP headers (unused)
   * @param status suggested status from caller (ignored; 404 enforced)
   * @return builder pre-populated with NOT_FOUND status
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.NOT_FOUND);
  }
}
