package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

public class MaxUploadSizeExceededMapping implements ExceptionMapping {

  private final DetailFormat detailFormat;

  public MaxUploadSizeExceededMapping(DetailFormat detailFormat) {
    this.detailFormat = detailFormat;
  }

  @Override
  public Class<MaxUploadSizeExceededException> getExceptionClass() {
    return MaxUploadSizeExceededException.class;
  }

  @Override
  public Problem map(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    MaxUploadSizeExceededException e = (MaxUploadSizeExceededException) ex;
    return Problem.builder()
        .status(ProblemStatus.CONTENT_TOO_LARGE)
        .detail(detailFormat.format("Max upload size exceeded"))
        .extension("max", e.getMaxUploadSize())
        .build();
  }
}
