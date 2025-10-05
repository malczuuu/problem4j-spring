package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

public class HttpMediaTypeNotAcceptableMapping implements ExceptionMapping {

  @Override
  public Class<HttpMediaTypeNotAcceptableException> getExceptionClass() {
    return HttpMediaTypeNotAcceptableException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.NOT_ACCEPTABLE).build();
  }
}
