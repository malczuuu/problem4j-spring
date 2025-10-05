package io.github.malczuuu.problem4j.spring.web.mapping;

import static io.github.malczuuu.problem4j.spring.web.internal.MethodParameterSupport.findParameterName;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PROPERTY_EXTENSION;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import java.util.Optional;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebInputException;

public class ServerWebInputMapping extends AbstractExceptionMapping {

  private final TypeMismatchMapping typeMismatchMapping;

  public ServerWebInputMapping(ProblemFormat problemFormat) {
    super(ServerWebInputException.class, problemFormat);
    typeMismatchMapping = new TypeMismatchMapping(problemFormat);
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ServerWebInputException swie = (ServerWebInputException) ex;

    if (ex.getCause() instanceof TypeMismatchException tme) {
      Problem problem = typeMismatchMapping.map(tme, headers, status);
      if (!problem.hasExtension(PROPERTY_EXTENSION)) {
        problem = tryAppendingPropertyFromMethodParameter(swie.getMethodParameter(), problem);
      }
      return problem;
    }

    return Problem.builder()
        .status(
            ProblemStatus.findValue(swie.getStatusCode().value())
                .orElse(ProblemStatus.INTERNAL_SERVER_ERROR))
        .build();
  }

  private Problem tryAppendingPropertyFromMethodParameter(
      MethodParameter parameter, Problem problem) {
    Optional<String> optionalProperty = findParameterName(parameter);
    if (optionalProperty.isPresent()) {
      problem = problem.toBuilder().extension(PROPERTY_EXTENSION, optionalProperty.get()).build();
    }
    return problem;
  }
}
