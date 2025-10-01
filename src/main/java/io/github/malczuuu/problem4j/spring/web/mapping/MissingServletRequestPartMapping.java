package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.formatting.DetailFormatting;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

final class MissingServletRequestPartMapping implements ExceptionMapping {

  private final DetailFormatting detailFormatting;

  MissingServletRequestPartMapping(DetailFormatting detailFormatting) {
    this.detailFormatting = detailFormatting;
  }

  @Override
  public Class<MissingServletRequestPartException> getExceptionClass() {
    return MissingServletRequestPartException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    MissingServletRequestPartException e = (MissingServletRequestPartException) ex;
    return Problem.builder()
        .status(ProblemStatus.BAD_REQUEST)
        .detail(detailFormatting.format("Missing request part"))
        .extension("param", e.getRequestPartName())
        .build();
  }
}
