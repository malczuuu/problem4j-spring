package io.github.malczuuu.problem4j.spring.webmvc;

import static io.github.malczuuu.problem4j.spring.web.util.InstanceSupport.overrideInstance;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.mapping.ConstraintViolationMapping;
import io.github.malczuuu.problem4j.spring.web.util.StaticProblemContext;
import io.github.malczuuu.problem4j.spring.web.util.TracingSupport;
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

  private final String instanceOverride;

  public ConstraintViolationExceptionMvcAdvice(
      ConstraintViolationMapping constraintViolationMapping, String instanceOverride) {
    this.constraintViolationMapping = constraintViolationMapping;
    this.instanceOverride = instanceOverride;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Problem> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    StaticProblemContext context =
        new StaticProblemContext(request.getAttribute(TracingSupport.TRACE_ID_ATTR, SCOPE_REQUEST));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    HttpStatus status = HttpStatus.BAD_REQUEST;

    Problem problem = constraintViolationMapping.map(ex, headers, status);
    problem = overrideInstance(problem, instanceOverride, context);

    status = HttpStatus.valueOf(problem.getStatus());

    return new ResponseEntity<>(problem, headers, status);
  }
}
