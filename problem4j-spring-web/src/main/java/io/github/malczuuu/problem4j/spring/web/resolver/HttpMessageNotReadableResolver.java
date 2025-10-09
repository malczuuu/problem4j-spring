package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;

/**
 * Handles {@link HttpMessageNotReadableException} thrown when an HTTP request body cannot be read
 * or parsed.
 *
 * <p>This typically occurs for requests with malformed JSON, XML, or other payloads that cannot be
 * converted by the configured {@code HttpMessageConverter}s.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 400 (Bad Request) response to
 * indicate that the request body is invalid or unreadable.
 *
 * @see org.springframework.http.converter.HttpMessageConverter
 */
public class HttpMessageNotReadableResolver extends AbstractProblemResolver {

  public HttpMessageNotReadableResolver(ProblemFormat problemFormat) {
    super(HttpMessageNotReadableException.class, problemFormat);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.BAD_REQUEST);
  }
}
