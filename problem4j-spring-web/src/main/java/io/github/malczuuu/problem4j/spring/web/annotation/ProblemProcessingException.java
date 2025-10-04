package io.github.malczuuu.problem4j.spring.web.annotation;

/**
 * Thrown when processing an annotated exception into a Problem fails. {@code @RestControllerAdvice}
 * or any other handlers can catch this and return a safe {@code 500 Problem}. No other exception is
 * thrown from {@link SimpleProblemMappingProcessor}.
 */
public class ProblemProcessingException extends RuntimeException {

  public ProblemProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
