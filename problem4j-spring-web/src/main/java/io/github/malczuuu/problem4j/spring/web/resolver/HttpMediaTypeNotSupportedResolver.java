package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.HttpMediaTypeNotSupportedException;

/**
 * Handles {@link HttpMediaTypeNotSupportedException} thrown when a client sends a request with a
 * media type that the server cannot consume.
 *
 * <p>This typically occurs when the {@code Content-Type} header in the HTTP request does not match
 * any of the media types supported by the controller method or configured message converters.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 415 (Unsupported Media Type)
 * response to inform the client that the submitted content type is not supported.
 */
public class HttpMediaTypeNotSupportedResolver extends AbstractProblemResolver {

  public HttpMediaTypeNotSupportedResolver(ProblemFormat problemFormat) {
    super(HttpMediaTypeNotSupportedException.class, problemFormat);
  }

  /**
   * Returns a {@link ProblemBuilder} with status {@link ProblemStatus#UNSUPPORTED_MEDIA_TYPE} (HTTP
   * 415). Other parameters are ignored because the status is mandated by the semantics of {@link
   * HttpMediaTypeNotSupportedException}.
   *
   * @param context problem context (unused)
   * @param ex the triggering {@link HttpMediaTypeNotSupportedException}
   * @param headers HTTP headers (unused)
   * @param status suggested status from caller (ignored; 415 enforced)
   * @return builder pre-populated with 415 status
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.UNSUPPORTED_MEDIA_TYPE);
  }
}
