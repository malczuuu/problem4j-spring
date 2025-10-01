package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handles Spring framework exceptions using registered {@link
 * io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping}s.
 *
 * <p>This class extends {@link ResponseEntityExceptionHandler} and overrides {@link
 * #handleExceptionInternal} to replace the response body with a {@link Problem} object.
 *
 * <p>Behavior:
 *
 * <ul>
 *   <li>Delegates exception-to-problem mapping to {@link ExceptionMappingRegistry}.
 *   <li>Sets content type to {@code application/problem+json}.
 *   <li>Falls back to {@link ProblemStatus#INTERNAL_SERVER_ERROR} if mapping fails.
 * </ul>
 */
@RestControllerAdvice
public class ProblemEnhancedExceptionHandler extends ResponseEntityExceptionHandler {

  private final ExceptionMappingRegistry exceptionMappingRegistry;

  public ProblemEnhancedExceptionHandler(ExceptionMappingRegistry exceptionMappingRegistry) {
    this.exceptionMappingRegistry = exceptionMappingRegistry;
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    Problem problem = overrideProblemResponseBody(ex, headers, status, request);

    headers = new HttpHeaders(headers);
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    return super.handleExceptionInternal(
        ex, problem, headers, HttpStatus.valueOf(problem.getStatus()), request);
  }

  private Problem overrideProblemResponseBody(
      Exception ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    try {
      return exceptionMappingRegistry
          .findMapping(ex.getClass())
          .map(mapping -> mapping.map(ex, headers, status, request))
          .orElseGet(() -> fallbackProblem(status));
    } catch (Exception e) {
      return fallbackProblem(status);
    }
  }

  private Problem fallbackProblem(HttpStatusCode status) {
    return Problem.builder()
        .status(ProblemStatus.findValue(status.value()).orElse(ProblemStatus.INTERNAL_SERVER_ERROR))
        .build();
  }
}
