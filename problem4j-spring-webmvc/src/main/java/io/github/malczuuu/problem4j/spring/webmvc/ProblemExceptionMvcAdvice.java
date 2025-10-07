package io.github.malczuuu.problem4j.spring.webmvc;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemException;
import io.github.malczuuu.problem4j.spring.web.tracing.TracingSupport;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles {@link ProblemException} thrown by application code.
 *
 * <p>Converts the exception into a {@link Problem} response with the appropriate HTTP status and
 * content type {@code application/problem+json}.
 *
 * <p>This is intended for application-level exceptions already represented as {@link Problem}.
 */
@RestControllerAdvice
public class ProblemExceptionMvcAdvice {

  @ExceptionHandler(ProblemException.class)
  public ResponseEntity<Problem> handleProblemException(ProblemException ex, WebRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem = ex.getProblem();

    Object instanceOverride = request.getAttribute(TracingSupport.INSTANCE_OVERRIDE, SCOPE_REQUEST);

    if (instanceOverride != null) {
      problem = problem.toBuilder().instance(instanceOverride.toString()).build();
    }

    HttpStatus status = ProblemSupport.resolveStatus(problem);

    return new ResponseEntity<>(problem, headers, status);
  }
}
