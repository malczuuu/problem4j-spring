package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MISSING_PATH_VARIABLE_DETAIL;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.NAME_EXTENSION;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerErrorException;

/**
 * Handles {@link ServerErrorException} thrown when a server-side error occurs while processing a
 * request.
 *
 * <p>This exception indicates that an unexpected condition or internal failure prevented the server
 * from fulfilling the request.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 5xx response (e.g., 500 Internal
 * Server Error) to inform the client that the request could not be processed due to a server-side
 * problem.
 */
public class ServerErrorProblemResolver extends AbstractProblemResolver {

  public ServerErrorProblemResolver(ProblemFormat problemFormat) {
    super(ServerErrorException.class, problemFormat);
  }

  /**
   * Resolves a {@link ServerErrorException} into a {@link ProblemBuilder}.
   *
   * <p>Special case: Spring WebFlux's {@code PathVariableMethodArgumentResolver} raises {@code
   * ServerErrorException} (instead of a missing-value exception) when a required {@code
   * PathVariable} is absent. In that scenario this method returns a BAD_REQUEST problem with a
   * standardized detail ({@code ProblemSupport#MISSING_PATH_VARIABLE_DETAIL}) and an extension
   * "{@code ProblemSupport#NAME_EXTENSION}" containing the variable name.
   *
   * <p>Otherwise, it falls back to a generic INTERNAL_SERVER_ERROR problem.
   *
   * @param context problem context (unused)
   * @param ex the triggering {@link ServerErrorException}
   * @param headers HTTP headers (unused)
   * @param status suggested status from caller (ignored; derives from condition)
   * @return builder with BAD_REQUEST + path variable info or INTERNAL_SERVER_ERROR
   * @see io.github.malczuuu.problem4j.spring.web.util.ProblemSupport#MISSING_PATH_VARIABLE_DETAIL
   * @see io.github.malczuuu.problem4j.spring.web.util.ProblemSupport#NAME_EXTENSION
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ServerErrorException e = (ServerErrorException) ex;

    if (isMissingPathVariableError(e)) {
      String name = findParameterName(e.getMethodParameter());
      return Problem.builder()
          .status(ProblemStatus.BAD_REQUEST)
          .detail(formatDetail(MISSING_PATH_VARIABLE_DETAIL))
          .extension(NAME_EXTENSION, name);
    }

    return Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Unlike other implementations of {@code AbstractNamedValueSyncArgumentResolver} that throw
   * {@code MissingRequestValueException} when certain {@code @RestController} argument is missing,
   * {@code PathVariableMethodArgumentResolver} throws {@code ServerErrorException}.
   *
   * <ul>
   *   <li>{@code MissingRequestValueException} (from {@code spring-web})
   *   <li>{@code ServerErrorException} (from {@code spring-web})
   *   <li>{@code AbstractNamedValueSyncArgumentResolver} (from {@code spring-webflux})
   *   <li>{@code PathVariableMethodArgumentResolver} (from {@code spring-webflux})
   * </ul>
   *
   * @see org.springframework.web.server.MissingRequestValueException
   * @see org.springframework.web.server.ServerErrorException
   * @return {@code true} if the exception actually refers to missing path variable, {@code false}
   *     otherwise
   */
  private boolean isMissingPathVariableError(ServerErrorException e) {
    return e.getHandlerMethod() != null
        && AnnotationUtils.findAnnotation(e.getHandlerMethod(), RequestMapping.class) != null
        && e.getMethodParameter() != null
        && e.getMethodParameter().hasParameterAnnotation(PathVariable.class);
  }

  /**
   * Derives a path variable's logical name from the {@link MethodParameter}. If the parameter is
   * annotated with {@link PathVariable} and its {@code name} attribute is non-empty, that value is
   * returned; otherwise the Java parameter name (which may be {@code null} if not compiled with
   * {@code -parameters}) is returned.
   *
   * @param methodParameter the method parameter describing the missing path variable (never null
   *     when invoked)
   * @return explicit annotation name, falling back to the reflective parameter name (may be {@code
   *     null})
   */
  private String findParameterName(MethodParameter methodParameter) {
    PathVariable annotation = methodParameter.getParameterAnnotation(PathVariable.class);
    return (annotation != null && StringUtils.hasLength(annotation.name()))
        ? annotation.name()
        : methodParameter.getParameterName();
  }
}
