package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MAX_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.MAX_UPLOAD_SIZE_EXCEEDED_DETAIL;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

public class MaxUploadSizeExceededResolver extends AbstractProblemResolver {

  public MaxUploadSizeExceededResolver(ProblemFormat problemFormat) {
    super(MaxUploadSizeExceededException.class, problemFormat);
  }

  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    MaxUploadSizeExceededException e = (MaxUploadSizeExceededException) ex;
    return Problem.builder()
        .status(ProblemStatus.CONTENT_TOO_LARGE)
        .detail(formatDetail(MAX_UPLOAD_SIZE_EXCEEDED_DETAIL))
        .extension(MAX_EXTENSION, e.getMaxUploadSize());
  }
}
