package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.context.request.WebRequest;

final class MethodValidationMapping implements ExceptionMapping {

  @Override
  public Class<MethodValidationException> getExceptionClass() {
    return MethodValidationException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    return Problem.builder()
        .status(ProblemStatus.findValue(status.value()).orElse(ProblemStatus.INTERNAL_SERVER_ERROR))
        .build();
  }
}
