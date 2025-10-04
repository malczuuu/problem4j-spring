package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerErrorException;

public class ServerErrorMapping implements ExceptionMapping {

  private final DetailFormat detailFormat;

  public ServerErrorMapping(DetailFormat detailFormat) {
    this.detailFormat = detailFormat;
  }

  @Override
  public Class<? extends Exception> getExceptionClass() {
    return ServerErrorException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ServerErrorException e = (ServerErrorException) ex;

    if (isMissingPathVariableError(e)) {
      String name = findParameterName(e.getMethodParameter());
      return Problem.builder()
          .status(ProblemStatus.BAD_REQUEST)
          .detail(detailFormat.format("Missing path variable"))
          .extension("name", name)
          .build();
    }

    return ProblemSupport.INTERNAL_SERVER_ERROR;
  }

  /**
   * Unlike other implementations of {@code AbstractNamedValueSyncArgumentResolver} that throw
   * {@code MissingRequestValueException} when certain {@code @RestController} argument is missing,
   * {@code PathVariableMethodArgumentResolver} throws {@code ServerErrorException}.
   *
   * @see org.springframework.web.server.MissingRequestValueException
   * @see org.springframework.web.server.ServerErrorException
   * @see
   *     org.springframework.web.reactive.result.method.annotation.AbstractNamedValueSyncArgumentResolver
   * @see
   *     org.springframework.web.reactive.result.method.annotation.PathVariableMethodArgumentResolver
   * @return {@code true} if the exception actually refers to missing path variable, {@code false}
   *     otherwise
   */
  private boolean isMissingPathVariableError(ServerErrorException e) {
    return e.getHandlerMethod() != null
        && AnnotationUtils.findAnnotation(e.getHandlerMethod(), RequestMapping.class) != null
        && e.getMethodParameter() != null
        && e.getMethodParameter().hasParameterAnnotation(PathVariable.class);
  }

  private String findParameterName(MethodParameter methodParameter) {
    PathVariable annotation = methodParameter.getParameterAnnotation(PathVariable.class);
    return (annotation != null && StringUtils.hasLength(annotation.name()))
        ? annotation.name()
        : methodParameter.getParameterName();
  }
}
