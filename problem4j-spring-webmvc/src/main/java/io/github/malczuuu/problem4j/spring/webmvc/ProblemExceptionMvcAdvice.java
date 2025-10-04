package io.github.malczuuu.problem4j.spring.webmvc;

import static io.github.malczuuu.problem4j.spring.web.util.InstanceSupport.overrideInstance;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemException;
import io.github.malczuuu.problem4j.spring.web.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.util.StaticProblemContext;
import io.github.malczuuu.problem4j.spring.web.util.TracingSupport;
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

  private final String instanceOverride;

  public ProblemExceptionMvcAdvice(String instanceOverride) {
    this.instanceOverride = instanceOverride;
  }

  @ExceptionHandler(ProblemException.class)
  public ResponseEntity<Problem> handleProblemException(ProblemException ex, WebRequest request) {
    ProblemContext context =
        new StaticProblemContext(request.getAttribute(TracingSupport.TRACE_ID_ATTR, SCOPE_REQUEST));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem = ex.getProblem();
    problem = overrideInstance(problem, instanceOverride, context);

    HttpStatus status = HttpStatus.valueOf(problem.getStatus());

    return new ResponseEntity<>(problem, headers, status);
  }
}
