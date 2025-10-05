package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebInputException;

public class ServerWebInputMapping implements ExceptionMapping {

  private final TypeMismatchMapping typeMismatchMapping;

  public ServerWebInputMapping(DetailFormat detailFormat) {
    typeMismatchMapping = new TypeMismatchMapping(detailFormat);
  }

  @Override
  public Class<ServerWebInputException> getExceptionClass() {
    return ServerWebInputException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    if (ex.getCause() instanceof TypeMismatchException e) {
      return typeMismatchMapping.map(e, headers, status);
    }

    ServerWebInputException e = (ServerWebInputException) ex;

    return Problem.builder()
        .status(
            ProblemStatus.findValue(e.getStatusCode().value())
                .orElse(ProblemStatus.INTERNAL_SERVER_ERROR))
        .build();
  }
}
