package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_REQUEST_PART_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PARAM_EXTENSION;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * Handles {@link MissingServletRequestPartResolver} related exceptions, thrown when a required part
 * of a multipart request is missing.
 *
 * <p>This typically occurs when a controller method parameter is annotated with
 * {@code @RequestPart} and the client fails to include the expected file or form part in a
 * multipart/form-data request.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 400 (Bad Request) response to
 * indicate that the required request part was not provided.
 *
 * <p>Unlike {@link ServletRequestBindingResolver}, this resolver is separate because its exception
 * ({@link MissingServletRequestPartException}} extends {@code ServletException} rather than {@code
 * ServletRequestBindingException}.
 *
 * @see jakarta.servlet.ServletException
 * @see org.springframework.web.bind.ServletRequestBindingException
 */
public class MissingServletRequestPartResolver extends AbstractProblemResolver {

  public MissingServletRequestPartResolver(ProblemFormat problemFormat) {
    super(MissingServletRequestPartException.class, problemFormat);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    MissingServletRequestPartException e = (MissingServletRequestPartException) ex;
    return Problem.builder()
        .status(ProblemStatus.BAD_REQUEST)
        .detail(formatDetail(MISSING_REQUEST_PART_DETAIL))
        .extension(PARAM_EXTENSION, e.getRequestPartName());
  }
}
