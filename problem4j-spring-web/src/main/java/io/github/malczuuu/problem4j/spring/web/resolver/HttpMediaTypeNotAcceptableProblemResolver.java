package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

/**
 * Handles {@link HttpMediaTypeNotAcceptableException} thrown when a client requests a response
 * media type that the server cannot produce.
 *
 * <p>This typically occurs when the {@code Accept} header in the HTTP request does not match any of
 * the media types supported by the controller method or configured message converters.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 406 (Not Acceptable) response to
 * inform the client that the requested content type is not available.
 */
public class HttpMediaTypeNotAcceptableProblemResolver extends AbstractProblemResolver {

  public HttpMediaTypeNotAcceptableProblemResolver(ProblemFormat problemFormat) {
    super(HttpMediaTypeNotAcceptableException.class, problemFormat);
  }

  /**
   * Returns a {@link ProblemBuilder} with {@link ProblemStatus#NOT_ACCEPTABLE} (HTTP 406). Other
   * parameters ({@code context}, {@code headers}, {@code status}) are ignored because the status is
   * dictated by the semantics of {@link HttpMediaTypeNotAcceptableException}.
   *
   * @param context problem context (unused)
   * @param ex the triggering {@link HttpMediaTypeNotAcceptableException}
   * @param headers HTTP headers (unused)
   * @param status suggested status from caller (ignored; 406 enforced)
   * @return builder pre-populated with 406 status
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.NOT_ACCEPTABLE);
  }
}
