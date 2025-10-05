package io.github.malczuuu.problem4j.spring.webmvc;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.internal.TracingSupport;
import io.github.malczuuu.problem4j.spring.web.mapping.ConstraintViolationMapping;
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

  private final ConstraintViolationMapping constraintViolationMapping;

  public ConstraintViolationExceptionMvcAdvice(
      ConstraintViolationMapping constraintViolationMapping) {
    this.constraintViolationMapping = constraintViolationMapping;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Problem> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Object instanceOverride =
        request.getAttribute(TracingSupport.INSTANCE_OVERRIDE_ATTR, SCOPE_REQUEST);

    HttpStatus status = HttpStatus.BAD_REQUEST;

    Problem problem = constraintViolationMapping.map(ex, headers, status);
    if (instanceOverride != null) {
      problem = problem.toBuilder().instance(instanceOverride.toString()).build();
    }

    status = ProblemSupport.resolveStatus(problem);

    return new ResponseEntity<>(problem, headers, status);
  }
}
