package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ResponseStatusMapping implements ExceptionMapping {

  @Override
  public Class<? extends Exception> getExceptionClass() {
    return ResponseStatusException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ResponseStatusException e = (ResponseStatusException) ex;
    return Problem.builder()
        .status(
            ProblemStatus.findValue(e.getStatusCode().value())
                .orElse(ProblemStatus.INTERNAL_SERVER_ERROR))
        .build();
  }
}
