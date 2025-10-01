package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.formatting.DetailFormatting;
import java.util.Locale;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;

final class MissingServletRequestParameterMapping implements ExceptionMapping {

  private final DetailFormatting detailFormatting;

  MissingServletRequestParameterMapping(DetailFormatting detailFormatting) {
    this.detailFormatting = detailFormatting;
  }

  @Override
  public Class<MissingServletRequestParameterException> getExceptionClass() {
    return MissingServletRequestParameterException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    MissingServletRequestParameterException e = (MissingServletRequestParameterException) ex;
    return Problem.builder()
        .status(ProblemStatus.BAD_REQUEST)
        .detail(detailFormatting.format("Missing request param"))
        .extension("param", e.getParameterName())
        .extension("kind", e.getParameterType().toLowerCase(Locale.ROOT))
        .build();
  }
}
