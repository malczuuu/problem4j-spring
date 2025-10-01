package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.formatting.DetailFormatting;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.context.request.WebRequest;

final class MissingPathVariableMapping implements ExceptionMapping {

  private final DetailFormatting detailFormatting;

  MissingPathVariableMapping(DetailFormatting detailFormatting) {
    this.detailFormatting = detailFormatting;
  }

  @Override
  public Class<MissingPathVariableException> getExceptionClass() {
    return MissingPathVariableException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    MissingPathVariableException e = (MissingPathVariableException) ex;
    return Problem.builder()
        .status(ProblemStatus.BAD_REQUEST)
        .detail(detailFormatting.format("Missing path variable"))
        .extension("name", e.getVariableName())
        .build();
  }
}
