package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * {@code MissingServletRequestPartMapping} is an {@link
 * io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping} implementation that maps {@link
 * MissingServletRequestPartException} to a {@link Problem} representation.
 *
 * <p>Unlike {@link ServletRequestBindingMapping}, this mapping is separate because {@link
 * MissingServletRequestPartException} extends {@link jakarta.servlet.ServletException} rather than
 * {@link org.springframework.web.bind.ServletRequestBindingException}.
 *
 * <p>The produced {@code Problem} has status {@code 400 Bad Request}, a human-readable detail
 * message determined by the configured {@link DetailFormat}, and an extension containing the
 * missing part name.
 *
 * @see MissingServletRequestPartException
 * @see Problem
 * @see ProblemStatus#BAD_REQUEST
 * @see ServletRequestBindingMapping
 */
public class MissingServletRequestPartMapping implements ExceptionMapping {

  private final DetailFormat detailFormat;

  /**
   * Creates a new {@code MissingServletRequestPartMapping} with the given {@link DetailFormat}
   * strategy.
   *
   * @param detailFormat the format strategy for problem detail messages; must not be {@code null}
   */
  public MissingServletRequestPartMapping(DetailFormat detailFormat) {
    this.detailFormat = detailFormat;
  }

  /**
   * Returns the exception class handled by this mapping.
   *
   * @return the exception type ({@link MissingServletRequestPartException}) that this mapping
   *     supports
   */
  @Override
  public Class<MissingServletRequestPartException> getExceptionClass() {
    return MissingServletRequestPartException.class;
  }

  /**
   * Maps the given {@link MissingServletRequestPartException} to a {@link Problem} response.
   *
   * <p>The returned {@link Problem} includes:
   *
   * <ul>
   *   <li>Status {@code 400 Bad Request}
   *   <li>A detail message formatted via the configured {@link DetailFormat}
   *   <li>An extension named {@code "param"} containing the missing request part name
   * </ul>
   *
   * @param ex the exception to map (never {@code null})
   * @param headers the HTTP headers of the response (ignored in this mapping)
   * @param status the HTTP status suggested by the framework (ignored, always {@code 400})
   * @return a {@link Problem} representation of the given exception
   */
  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    MissingServletRequestPartException e = (MissingServletRequestPartException) ex;
    return Problem.builder()
        .status(ProblemStatus.BAD_REQUEST)
        .detail(detailFormat.format("Missing request part"))
        .extension("param", e.getRequestPartName())
        .build();
  }
}
