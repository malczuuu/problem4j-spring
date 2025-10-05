package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ResponseStatusMapping extends AbstractExceptionMapping {

  public ResponseStatusMapping(ProblemFormat problemFormat) {
    super(ResponseStatusException.class, problemFormat);
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
