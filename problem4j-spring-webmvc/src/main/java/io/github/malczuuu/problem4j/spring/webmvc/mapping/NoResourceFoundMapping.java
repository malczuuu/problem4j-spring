package io.github.malczuuu.problem4j.spring.webmvc.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.servlet.resource.NoResourceFoundException;

public class NoResourceFoundMapping implements ExceptionMapping {

  @Override
  public Class<NoResourceFoundException> getExceptionClass() {
    return NoResourceFoundException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.NOT_FOUND).build();
  }
}
