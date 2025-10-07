package io.github.malczuuu.problem4j.spring.webmvc;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.resolver.ConstraintViolationResolver;
import io.github.malczuuu.problem4j.spring.web.tracing.TracingSupport;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ConstraintViolationExceptionMvcAdvice {

  private final ConstraintViolationResolver constraintViolationResolver;

  public ConstraintViolationExceptionMvcAdvice(
      ConstraintViolationResolver constraintViolationResolver) {
    this.constraintViolationResolver = constraintViolationResolver;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Problem> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    ProblemContext context =
        ProblemContext.builder()
            .traceId(request.getAttribute(TracingSupport.TRACE_ID_ATTR, SCOPE_REQUEST))
            .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Object instanceOverride =
        request.getAttribute(TracingSupport.INSTANCE_OVERRIDE_ATTR, SCOPE_REQUEST);

    HttpStatus status = HttpStatus.BAD_REQUEST;

    ProblemBuilder builder = constraintViolationResolver.resolve(context, ex, headers, status);
    if (instanceOverride != null) {
      builder = builder.instance(instanceOverride.toString());
    }
    Problem problem = builder.build();

    status = ProblemSupport.resolveStatus(problem);

    return new ResponseEntity<>(problem, headers, status);
  }
}
