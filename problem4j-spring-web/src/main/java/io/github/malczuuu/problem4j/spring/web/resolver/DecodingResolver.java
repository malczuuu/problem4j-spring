package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * Thrown when the request body cannot be decoded (e.g. malformed JSON or invalid * content type).
 * Maps such errors to a {@code Problem} with {@code 400 Bad Request} status.
 *
 * <p>Maps decoding failures (e.g. malformed JSON or invalid request bodies) to a {@code Problem}
 * response with {@code 400 Bad Request} status.
 */
public class DecodingResolver extends AbstractProblemResolver {

  public DecodingResolver(ProblemFormat problemFormat) {
    super(DecodingException.class, problemFormat);
  }

  /**
   * Builds a {@link ProblemBuilder} for {@link DecodingException} with {@code BAD_REQUEST} status.
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.BAD_REQUEST);
  }
}
