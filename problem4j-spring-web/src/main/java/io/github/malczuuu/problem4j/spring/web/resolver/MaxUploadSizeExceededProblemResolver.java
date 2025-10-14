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

/**
 * Handles {@link MaxUploadSizeExceededException} thrown when a file upload exceeds the configured
 * maximum size limit.
 *
 * <p>This occurs during multipart/form-data requests if the uploaded file is larger than the limit
 * set in the server or Spring configuration (e.g., {@code spring.servlet.multipart.max-file-size}).
 *
 * <p>The handler is responsible for returning an appropriate HTTP 413 (Payload Too Large) response
 * to inform the client that the uploaded file exceeds the allowed size.
 */
public class MaxUploadSizeExceededProblemResolver extends AbstractProblemResolver {

  public MaxUploadSizeExceededProblemResolver(ProblemFormat problemFormat) {
    super(MaxUploadSizeExceededException.class, problemFormat);
  }

  /**
   * Builds a {@link ProblemBuilder} with status {@link ProblemStatus#CONTENT_TOO_LARGE}, a
   * formatted standard detail message, and an extension entry providing the maximum allowed upload
   * size. Other parameters ({@code context}, {@code headers}, {@code status}) are ignored because
   * the semantics of {@link MaxUploadSizeExceededException} dictate the response.
   *
   * @param context problem context (unused for this resolver)
   * @param ex the triggering {@link MaxUploadSizeExceededException}
   * @param headers HTTP headers (unused)
   * @param status suggested status from caller (ignored; 413 enforced)
   * @return builder pre-populated with status, detail, and max size extension
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    MaxUploadSizeExceededException e = (MaxUploadSizeExceededException) ex;
    ProblemBuilder builder =
        Problem.builder()
            .status(ProblemStatus.CONTENT_TOO_LARGE)
            .detail(formatDetail(MAX_UPLOAD_SIZE_EXCEEDED_DETAIL));
    if (e.getMaxUploadSize() > 0) {
      builder.extension(MAX_EXTENSION, e.getMaxUploadSize());
    }
    return builder;
  }
}
