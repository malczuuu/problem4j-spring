package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.HttpRequestMethodNotSupportedException;

public class HttpRequestMethodNotSupportedMapping implements ExceptionMapping {

  @Override
  public Class<HttpRequestMethodNotSupportedException> getExceptionClass() {
    return HttpRequestMethodNotSupportedException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.METHOD_NOT_ALLOWED).build();
  }
}
