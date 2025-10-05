package io.github.malczuuu.problem4j.spring.web.mapping;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_PART_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PARAM_EXTENSION;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
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
 * message, and an extension containing the missing part name.
 *
 * @see MissingServletRequestPartException
 * @see Problem
 * @see ProblemStatus#BAD_REQUEST
 * @see ServletRequestBindingMapping
 */
public class MissingServletRequestPartMapping extends AbstractExceptionMapping {

  public MissingServletRequestPartMapping(ProblemFormat problemFormat) {
    super(MissingServletRequestPartException.class, problemFormat);
  }

  /**
   * Maps the given {@link MissingServletRequestPartException} to a {@link Problem} response.
   *
   * <p>The returned {@link Problem} includes:
   *
   * <ul>
   *   <li>Status {@code 400 Bad Request}
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
        .detail(formatDetail(MISSING_REQUEST_PART_DETAIL))
        .extension(PARAM_EXTENSION, e.getRequestPartName())
        .build();
  }
}
