package io.github.malczuuu.problem4j.spring.webmvc;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
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
 * Fallback exception handler for uncaught {@link Exception}s in Spring REST controllers.
 *
 * <p>This class uses {@link RestControllerAdvice} to intercept any exceptions not handled by more
 * specific exception handlers. It converts them into a standardized {@link Problem} response with:
 *
 * <ul>
 *   <li>HTTP status: {@link HttpStatus#INTERNAL_SERVER_ERROR}
 *   <li>Response body: a {@link Problem} object containing the status code and reason phrase
 *   <li>Content type: {@code application/problem+json}
 * </ul>
 *
 * <p>Intended as a **generic fallback**, it ensures that unexpected exceptions still produce a
 * consistent Problem+JSON response. For more specific exception handling, use {@link
 * ProblemEnhancedMvcHandler}, {@link ProblemExceptionMvcAdvice}.
 */
@RestControllerAdvice
public class ExceptionMvcAdvice {

  private final ProblemMappingProcessor problemMappingProcessor;

  public ExceptionMvcAdvice(ProblemMappingProcessor problemMappingProcessor) {
    this.problemMappingProcessor = problemMappingProcessor;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception ex, WebRequest request) {
    ProblemContext context =
        new StaticProblemContext(request.getAttribute(TracingSupport.TRACE_ID_ATTR, SCOPE_REQUEST));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Object instanceOverride =
        request.getAttribute(TracingSupport.INSTANCE_OVERRIDE_ATTR, SCOPE_REQUEST);

    Problem problem;

    if (problemMappingProcessor.isAnnotated(ex)) {
      problem = problemMappingProcessor.toProblem(ex, context);
      if (instanceOverride != null) {
        problem = problem.toBuilder().instance(instanceOverride.toString()).build();
      }
    } else {
      ProblemBuilder builder = Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR);
      if (instanceOverride != null) {
        builder = builder.instance(instanceOverride.toString());
      }
      problem = builder.build();
    }

    HttpStatus status = HttpStatus.valueOf(problem.getStatus());

    return new ResponseEntity<>(problem, headers, status);
  }
}
