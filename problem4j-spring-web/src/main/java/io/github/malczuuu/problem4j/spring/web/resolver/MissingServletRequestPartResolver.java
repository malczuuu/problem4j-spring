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
 * Unlike {@link ServletRequestBindingResolver}, this resolver is separate because its exception
 * ({@link MissingServletRequestPartException}} extends {@link jakarta.servlet.ServletException}
 * rather than {@link org.springframework.web.bind.ServletRequestBindingException}.
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
