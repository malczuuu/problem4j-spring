package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;

public class HttpMessageNotReadableMapping extends AbstractExceptionMapping {

  public HttpMessageNotReadableMapping(ProblemFormat problemFormat) {
    super(HttpMessageNotReadableException.class, problemFormat);
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.BAD_REQUEST).build();
  }
}
