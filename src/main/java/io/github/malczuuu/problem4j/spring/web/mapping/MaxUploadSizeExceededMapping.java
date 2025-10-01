package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.formatting.DetailFormatting;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

final class MaxUploadSizeExceededMapping implements ExceptionMapping {

  private final DetailFormatting detailFormatting;

  MaxUploadSizeExceededMapping(DetailFormatting detailFormatting) {
    this.detailFormatting = detailFormatting;
  }

  @Override
  public Class<MaxUploadSizeExceededException> getExceptionClass() {
    return MaxUploadSizeExceededException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    MaxUploadSizeExceededException e = (MaxUploadSizeExceededException) ex;
    return Problem.builder()
        .status(ProblemStatus.CONTENT_TOO_LARGE)
        .detail(detailFormatting.format("Max upload size exceeded"))
        .extension("max", e.getMaxUploadSize())
        .build();
  }
}
