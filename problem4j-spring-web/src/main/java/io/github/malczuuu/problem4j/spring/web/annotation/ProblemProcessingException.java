package io.github.malczuuu.problem4j.spring.web.annotation;

/**
 * Thrown when processing an annotated exception into a Problem fails. {@code @RestControllerAdvice}
 * or any other handlers can catch this and return a safe {@code 500 Problem}. No other exception is
 * thrown from {@link SimpleProblemMappingProcessor}.
 */
public class ProblemProcessingException extends RuntimeException {

  public ProblemProcessingException() {
    super();
  }

  public ProblemProcessingException(String message) {
    super(message);
  }

  public ProblemProcessingException(Throwable cause) {
    super(cause);
  }

  public ProblemProcessingException(String message, Throwable cause) {
    super(message, cause);
  }

  protected ProblemProcessingException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
